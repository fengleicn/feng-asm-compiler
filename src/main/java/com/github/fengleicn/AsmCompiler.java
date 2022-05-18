package com.github.fengleicn;

import cn.hutool.core.util.StrUtil;
import com.github.fengleicn.ClassLoadUtil;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import org.objectweb.asm.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;


/**
 * AsmCompiler 编译器和 JavaCompiler 类似, 但ASM编译器是可以不依赖 classpath 和 tools.jar 的
 */
public class AsmCompiler {
    private static final Map<String, Class<?>> cache = new ConcurrentHashMap<>();

    public static List<Class<?>> fromSource(String source) {
        final String DynamicUuid = "DynamicUuid";   // 动态类避免同名类
        int suffixLength = 32 + DynamicUuid.length(); // DynamicUuid + 32 uuid
        JavaParser javaParserBuild = new JavaParser();
        ParseResult<CompilationUnit> parse = javaParserBuild.parse(source);
        CompilationUnit compilationUnit = parse.getResult().orElseThrow(RuntimeException::new);
        List<? extends Class<?>> classes = compilationUnit.getTypes().stream().map(e -> AsmCompiler.typeToClass(
                (ClassOrInterfaceDeclaration) e)).collect(Collectors.toList());
        return classes.stream().peek(entry -> {
            if (entry.getName().contains(DynamicUuid)) {
                String name = entry.getName().substring(0, entry.getName().length() - suffixLength);
                cache.put(name, entry);
            }
        }).collect(Collectors.toList());
    }

    public static <T> Class<T> forName(String className) throws ClassNotFoundException {
        Class<?> clazz = cache.get(className);
        if (clazz == null) {
            throw new ClassNotFoundException(className);
        }
        return (Class<T>) clazz;
    }

    public static void main(String[] args) {
        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;
        classWriter.visit(V1_8, ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE, "com/shuyilink/lowcode/javaclassutil/TestTable919MapperDynamicUuid47cfadb1d62d444b8f09cf159f2e9c61", "Ljava/lang/Object;Lcom/shuyilink/mes/common/util/SimpleMapper<Lcom/shuyilink/lowcode/javaclassutil/TestTable919DynamicUuid47cfadb1d62d444b8f09cf159f2e9c61;>;", "java/lang/Object", new String[]{"com/shuyilink/mes/common/util/SimpleMapper"});
        classWriter.visitSource("TestTable919MapperDynamicUuid47cfadb1d62d444b8f09cf159f2e9c61.java", null);
        classWriter.visitEnd();
        byte[] bytes = classWriter.toByteArray();
        Class cl = ClassLoadUtil.load("com.shuyilink.lowcode.javaclassutil.TestTable919MapperDynamicUuid47cfadb1d62d444b8f09cf159f2e9c61", bytes);
        System.out.println(cl);
    }

    private static Class<?> typeToClass(ClassOrInterfaceDeclaration type) {
        String className = type.getFullyQualifiedName().orElseThrow(RuntimeException::new).replace(".", "/");
        NodeList<AnnotationExpr> annotations = type.getAnnotations();
        List<List<String>> annotationStringList = annotations
                .stream()
                .map(e -> Arrays.asList(e.getName().getQualifier().orElseThrow(RuntimeException::new) + "." + e.getName().getIdentifier()))
                .collect(Collectors.toList());

        AnnotationExpr tableAnnotation = annotations.stream().filter(e -> e.getName().toString().equals("javax.persistence.Table")).findFirst().orElse(null);


        MemberValuePair memberValuePair = null;
        String annotationClassName = null;
        String name = null;
        String value = null;
        if (tableAnnotation != null) {
            memberValuePair = (MemberValuePair) (tableAnnotation.getChildNodes().get(1));
            annotationClassName = tableAnnotation.getName().toString().replace(".", "/");
            name = memberValuePair.getName().toString();
            value = memberValuePair.getValue().toString().replace("\"", "" );
        }

        List<FieldDeclaration> fields = type.getFields();
        List<VariableDeclarator> fieldList = fields.stream().map(e -> e.getVariables().get(0)).collect(Collectors.toList());
        String superClassName = type.getExtendedTypes().get(0).toString().replace(".", "/");
        boolean isInterface = type.isInterface();

        String superGenericsClassName = null;
        if (superClassName.contains("<")) {
            String[] split = superClassName.split("<");
            String part1 = split[0];
            superGenericsClassName = split[1].replace(">", "");
            superClassName = part1;
        }

        System.out.println("className: " + className);
        System.out.println("annotationStringList: " + annotationStringList);
        System.out.println("fieldList: " + fieldList);
        System.out.println("superClassName: " + superClassName);
        System.out.println("superGenericsClassName: " + superGenericsClassName);

        ClassWriter classWriter = new ClassWriter(0);
        if (isInterface) {
            String sig = superClassName;
            if (StrUtil.isNotBlank(superGenericsClassName)) {
                sig = "Ljava/lang/Object;" + "L" + superClassName + "<L" + superGenericsClassName + ";>;";
            }
            classWriter.visit(V1_8,
                    ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE,
                    className,
                    sig,
                    "java/lang/Object",
                    new String[]{superClassName});
        } else {

            MethodVisitor methodVisitor;
            classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, null, superClassName, null);
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/shuyilink/mes/common/entity/BaseEntity", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "L" + className + ";", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();

            AnnotationVisitor annotationVisitor0;
            {
                annotationVisitor0 = classWriter.visitAnnotation("L"+annotationClassName+";", true);
                annotationVisitor0.visit(name, value);
                annotationVisitor0.visitEnd();
            }
        }

        fieldList.forEach(e -> {
            String fieldType = "L" + e.getType().asString().replace(".", "/") + ";";
            String fieldName = e.getName().getId();
            System.out.println("fieldType: " + fieldType + " fieldName: " + fieldName);
            classWriter.visitField(ACC_PUBLIC, fieldName, fieldType, null, null);
        });

        classWriter.visitEnd();
        byte[] bytes = classWriter.toByteArray();
        return ClassLoadUtil.load(className, bytes);
    }


}
