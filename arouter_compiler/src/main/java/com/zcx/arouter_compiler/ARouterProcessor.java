package com.zcx.arouter_compiler;


import com.zcx.arouter_annotation.ARouter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.zcx.arouter_annotation.ARouterBean;

@SupportedOptions({ProcessorConfig.OPTIONS, ProcessorConfig.APT_PACKAGE})
//接受moule 名字和包名，包名用来存储生成代码的存放位置
@AutoService(Process.class)
public class ARouterProcessor extends AbstractProcessor {
    StringBuilder importStr = new StringBuilder();
    Filer filer;
    Elements elementTool;
    Messager messager;//日志输入控件
    Types typeTool;
    String groupStr = "";
    String packageName = "";
    //存放所有的添加了注解的类的注解信息，
    private Map<String, List<ARouterBean>> mAllPathMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add("com.zcx.arouter_annotation.ARouter");
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
//        messager.printMessage(Diagnostic.Kind.NOTE, "init: ARouterProcessor");
        filer = processingEnvironment.getFiler();
        elementTool = processingEnvironment.getElementUtils();
        typeTool = processingEnvironment.getTypeUtils();
        String moudleName = processingEnvironment.getOptions().get(ProcessorConfig.OPTIONS);
//        messager.printMessage(Diagnostic.Kind.NOTE, "moudleName:" + moudleName);
        packageName = processingEnvironment.getOptions().get(ProcessorConfig.APT_PACKAGE);
//        messager.printMessage(Diagnostic.Kind.NOTE, "packageName:" + packageName);
        if (moudleName == null || packageName == null) {
            messager.printMessage(Diagnostic.Kind.NOTE, "请在你的build.gradle 文件的 defaultConfig 节点内添加 moduleName 和 packageNameForAPT 属性");
        }


    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);


        if (elements != null) {
            // Activity type
            TypeMirror activityTypeMirror = elementTool.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE).asType();

            for (Element element : elements) {

                /**
                 * 获取注解所在类的包名
                 */
                packageName = elementTool.getPackageOf(element).getQualifiedName().toString();
                importStr.append("import ").append(packageName).append(".").append(element.getSimpleName()).append(";\n");
                messager.printMessage(Diagnostic.Kind.NOTE, "packageName===>" + packageName);
                messager.printMessage(Diagnostic.Kind.NOTE, "SimpleName===>" + element.getSimpleName());
                //拿到每一个添加了ARouter 注解的类节点对象封装成ARouterBean
                ARouter aRouter = element.getAnnotation(ARouter.class);

                messager.printMessage(Diagnostic.Kind.NOTE, "path==>" + aRouter.Path());

                //检查开发者提供的path 和Group 是否符合规范  /app/MainActivity

                String path = aRouter.Path();
                String group = aRouter.Group();
                if (path.length() <= 0 || path.contains(" ")) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "path is not null or ‘ ’");
                    return false;
                }
                if (!path.startsWith("/") || path.endsWith("/")) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "path must startWith / and not endWith /");
                    return false;
                }
                if (path.lastIndexOf("/") == 0) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "@ARouter 注解没有按照规范配置 ：/app/MainActivity");
                    return false;
                }

                //从path中截取moudleName,作为Group 并设置group

                String[] split = path.split("/");
                messager.printMessage(Diagnostic.Kind.NOTE, "group==" + split[0]);
                messager.printMessage(Diagnostic.Kind.NOTE, "path==" + split[1]);
                ARouterBean bean;

                if (group.equals(split[1])) {
                    bean = new ARouterBean.Builder().addGroup(aRouter.Group()).addPath(aRouter.Path()).addElement(element).build();
                    groupStr = group;
                } else {
                    bean = new ARouterBean.Builder().addGroup(split[1]).addPath(aRouter.Path()).addElement(element).build();
                    groupStr = split[1];
                }

                messager.printMessage(Diagnostic.Kind.NOTE, "group===>" + groupStr);
                //判断当前注解的类是否是Activity
                TypeMirror elementTypeMirror = element.asType();//得到当前注解的类的类型

                if (typeTool.isSubtype(elementTypeMirror, activityTypeMirror)) {
                    bean.setBeanType(ARouterBean.BeanType.ACTIVITY);
                } else {
                    messager.printMessage(Diagnostic.Kind.NOTE, "@ARouter注解只支持在Activity类型");
                }

                List<ARouterBean> aRouterBeans = mAllPathMap.get(bean.getGroup());
                if (aRouterBeans == null) {
                    aRouterBeans = new ArrayList<>();
                    aRouterBeans.add(bean);
                    mAllPathMap.put(bean.getGroup(), aRouterBeans);
                } else {
                    aRouterBeans.add(bean);
                }
                /**
                 * 获取注解所在类类名
                 String typeN = element.getSimpleName().toString();
                 messager.printMessage(Diagnostic.Kind.NOTE, "typeN===>" + typeN); */
            }
            try {
                createPathFile(groupStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    private void createPathFile(String groupStr) throws IOException {

        List<ARouterBean> aRouterBeans = mAllPathMap.get(groupStr);
        if (aRouterBeans != null && aRouterBeans.size() > 0) {
            messager.printMessage(Diagnostic.Kind.NOTE, "packageName===>" + packageName);
            JavaFileObject sourceFile = filer.createSourceFile(packageName + ".ARouter$$Path$$" + groupStr);
            Writer openWriter = sourceFile.openWriter();
            BufferedWriter bufferedWriter = new BufferedWriter(openWriter);

            bufferedWriter.write("package " + packageName + ";\n\n");//写包名


//            bufferedWriter.write(importStr.toString());//
            bufferedWriter.write("import com.zcx.arouter_annotation.ARouterBean; \n");//
            bufferedWriter.write("import com.zcx.arouter_api.ARouterPath; \n");//
            bufferedWriter.write("import static com.zcx.arouter_annotation.ARouterBean.BeanType.ACTIVITY; \n");//
//        bufferedWriter.write("import java.lang.Override; \n");//
            bufferedWriter.write("import java.util.HashMap; \n");//
            bufferedWriter.write("import java.lang.String; \n");//
            bufferedWriter.write("import java.util.Map; \n\n");//
            bufferedWriter.write("public class ARouter$$Path$$" + groupStr + " implements ARouterPath { \n");//
            bufferedWriter.write("   @Override \n");//
            bufferedWriter.write("   public Map<String,ARouterBean> getPathMap() {\n");//
            bufferedWriter.write("       Map<String, ARouterBean> pathMap = new HashMap<>();\n");
            for (int i = 0; i < aRouterBeans.size(); i++) {
                ARouterBean bean = aRouterBeans.get(i);
                bean.getElement().getSimpleName();
                elementTool.getPackageOf(bean.getElement()).getQualifiedName();
                StringBuilder stringBuilder = new StringBuilder("       pathMap.put(")
                .append("\"")
                .append(bean.getPath())
                .append("\"")
                .append(",new ARouterBean.Builder().addGroup(")
                .append("\"")
                .append(groupStr)
                .append("\"")
                .append(").addPath(")
                .append("\"")
                .append(bean.getPath())
                .append("\"")
                .append(").addBeanType(")
                .append(bean.getBeanType())
                .append(").addClass(")
                .append(elementTool.getPackageOf(bean.getElement()).getQualifiedName() + "." + bean.getElement().getSimpleName())
                .append(".class).build());\n");
                bufferedWriter.write(stringBuilder.toString());
            }
            bufferedWriter.write("       return pathMap;\n");

            bufferedWriter.write("   }\n");
            bufferedWriter.write("}\n");
            bufferedWriter.close();


            createGroupFile(groupStr);
        }
    }


    /**
     * 创建 ARouter$$Group$$me   类文件
     *
     * @param groupStr
     * @throws IOException
     */

    private void createGroupFile(String groupStr) throws IOException {
        /**
         * import com.xiangxue.arouter_api.ARouterGroup;
         * import com.xiangxue.arouter_api.ARouterPath;
         * import java.lang.Class;
         * import java.lang.Override;
         * import java.lang.String;
         * import java.util.HashMap;
         * import java.util.Map;
         *
         * public class ARouter$$Group$$order implements ARouterGroup {
         *   @Override
         *   public Map<String, Class<? extends ARouterPath>> getGroupMap() {
         *     Map<String, Class<? extends ARouterPath>> groupMap = new HashMap<>();
         *     groupMap.put("order", ARouter$$Path$$order.class);
         *     return groupMap;
         *   }
         * }
         */

        JavaFileObject surFile = filer.createSourceFile(packageName + ".ARouter$$Group$$" + groupStr);
        Writer groupWriter = surFile.openWriter();
        BufferedWriter groupBufferedWriter = new BufferedWriter(groupWriter);

        groupBufferedWriter.write("package " + packageName + ";\n\n");//写包名
        //  * import com.xiangxue.arouter_api.ARouterGroup;
        groupBufferedWriter.write("import com.zcx.arouter_api.ARouterGroup; \n");//
        // import com.xiangxue.arouter_api.ARouterPath;
        groupBufferedWriter.write("import com.zcx.arouter_api.ARouterPath; \n");//
//                    import java.lang.Class;
        groupBufferedWriter.write("import java.lang.Class; \n");////
//        groupBufferedWriter.write("import java.lang.Override; \n");////
        groupBufferedWriter.write("import java.util.HashMap; \n");//
        groupBufferedWriter.write("import java.lang.String; \n");//
        groupBufferedWriter.write("import java.util.Map; \n\n");//
        groupBufferedWriter.write("public class ARouter$$Group$$" + groupStr + " implements ARouterGroup { \n");//
        groupBufferedWriter.write("   @Override \n");//
        groupBufferedWriter.write("   public Map<String,Class<? extends ARouterPath>> getGroup() {\n");//
        groupBufferedWriter.write("        Map<String, Class<? extends ARouterPath>> groupMap = new HashMap<>();\n");
        StringBuilder strGroupBuilder = new StringBuilder("        groupMap.put(");
        strGroupBuilder.append("\"");
        strGroupBuilder.append(groupStr);
        strGroupBuilder.append("\"");
        strGroupBuilder.append(",ARouter$$Path$$");
        strGroupBuilder.append(groupStr);
        strGroupBuilder.append(".class);\n        return groupMap;\n");
        groupBufferedWriter.write(strGroupBuilder.toString());
        groupBufferedWriter.write("   }\n");
        groupBufferedWriter.write("}\n");
        groupBufferedWriter.close();
    }


}
