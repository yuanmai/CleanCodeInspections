package me.maiyuan.cleancode;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import com.intellij.psi.controlFlow.DefUseUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ReferenceDeclarationDistanceInspection extends BaseJavaLocalInspectionTool {

    public static final int MAX_DISTANCE = 1000; //can not get line number from the API

    private static PsiCodeBlock lookupCodeBlock(PsiElement element) {
        PsiElement context = element.getContext();
        return context instanceof PsiCodeBlock ? (PsiCodeBlock) context : lookupCodeBlock(context);
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                PsiElement[] refs = DefUseUtil.getRefs(lookupCodeBlock(variable), variable,
                        variable.getInitializer());

                for (PsiElement ref : refs) {
                    if (ref.getTextOffset() - variable.getTextOffset() > MAX_DISTANCE) {
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
