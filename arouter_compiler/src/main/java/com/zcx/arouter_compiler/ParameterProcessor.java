package com.zcx.arouter_compiler;

import com.google.auto.service.AutoService;
import com.zcx.arouter_annotation.Paramenter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes({"com.zcx.arouter_annotation.Paramenter"})

@AutoService(Process.class)
public class ParameterProcessor extends AbstractProcessor {

    private Messager messager;
    Filer filer;
    Elements elementTool;
    String packName;
    TypeElement typeElement;
    // key:类节点, value:被@Parameter注解的属性集合
    private Map<TypeElement, List<Element>> tempParameter = new HashMap<>();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        elementTool = processingEnvironment.getElementUtils();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Paramenter.class);
        if (elements == null || elements.size() == 0) {
            messager.printMessage(Diagnostic.Kind.NOTE, "没有使用到@Paramenter注解的地方");
            return false;
        }

        for (Element element : elements) {
//            messager.printMessage(Diagnostic.Kind.NOTE, element.getSimpleName());

            packName = elementTool.getPackageOf(element).toString();
//            messager.printMessage(Diagnostic.Kind.NOTE, "pack===" + packName);
            //获取类节点

            typeElement = (TypeElement) element.getEnclosingElement();
//            messager.printMessage(Diagnostic.Kind.NOTE, "pack===" + typeElement.getSimpleName());

            if (tempParameter.containsKey(typeElement)) {
                List<Element> elementList = tempParameter.get(typeElement);
                elementList.add(element);
            } else {
                List<Element> elementList = new ArrayList<>();
                elementList.add(element);
                tempParameter.put(typeElement, elementList);
            }


        }


        for (Map.Entry<TypeElement, List<Element>> entrySet : tempParameter.entrySet()) {

            TypeElement key = entrySet.getKey();
            try {
                StringBuilder stringBuilder = new StringBuilder();
                String pageName = elementTool.getPackageOf(key.getEnclosingElement()).getQualifiedName().toString();


                JavaFileObject sourceFile = filer.createSourceFile(pageName + "." + key.getSimpleName() + "$$Paramenter");
                BufferedWriter   bufferedWriter = new BufferedWriter(sourceFile.openWriter());
                bufferedWriter.write("package " + pageName);
                bufferedWriter.write(";\nimport com.zcx.arouter_api.IParamenter;\n");
                bufferedWriter.write("import java.lang.Object;\n");
                bufferedWriter.write("import java.lang.Override;\n\n");

                stringBuilder.append("public class ").append(key.getSimpleName()).append("$$Paramenter").append(" implements IParamenter {\n").append("    @Override\n").append("    public void getParamenter(Object object){\n        ").append(key.getSimpleName()).append(" t = (").append(key.getSimpleName()).append(")object;\n");
                messager.printMessage(Diagnostic.Kind.NOTE, "pageName==========>" + pageName);

                List<Element> entrySetValue = entrySet.getValue();
                for (Element element : entrySetValue) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "==========>" + element.getSimpleName());
                    stringBuilder.append("        t.").append(element.getSimpleName()).append(" = t.getIntent().getStringExtra(").append("\"").append(element.getSimpleName()).append("\"").append(");\n");
                }

                stringBuilder.append("    }\n").append("}\n");
                bufferedWriter.write(stringBuilder.toString());
                bufferedWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


}
