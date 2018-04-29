package me.maiyuan.cleancode;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import static com.intellij.psi.controlFlow.DefUseUtil.getRefs;

public class ReferenceDeclarationDistanceInspection extends BaseJavaLocalInspectionTool {

    public static final int MAX_DISTANCE = 40;

    private static PsiCodeBlock lookupCodeBlock(PsiElement element) {
        PsiElement context = element.getContext();
        return context instanceof PsiCodeBlock ? (PsiCodeBlock) context : lookupCodeBlock(context);
    }

    private int getLineNumber(PsiElement element) {
        Document document = PsiDocumentManager
                .getInstance(element.getProject())
                .getDocument(element.getContainingFile());

        return document.getLineNumber(element.getTextOffset());
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                PsiCodeBlock codeBlock = lookupCodeBlock(variable);
                if (codeBlock == null || variable.getInitializer() == null) {
                    return;
                }

                int variableLineNum = getLineNumber(variable);

                for (PsiElement ref : getRefs(codeBlock, variable, variable.getInitializer())) {
                    if (getLineNumber(ref) - variableLineNum > MAX_DISTANCE) {
                        holder.registerProblem(ref.getOriginalElement(), "Reference is too far away from declaration. The declaration and reference statements should be roughly in the same screen.", ProblemHighlightType.WEAK_WARNING);
                    }
                }
            }

        };
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "Clean Code";
    }


    @NotNull
    @Override
    public String getShortName() {
        return this.getClass().getName();
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Reference declaration distance";
    }

}
