package com.fuser.config;

import java.util.List;
import com.tekai.Expression;
import static com.tekai.standard.CommonTransformation.from;
import static com.tekai.standard.CommonTransformation.fromType;
import static com.tekai.standard.CommonTransformation.fromValue;
import com.tekai.Transformation;
import com.tekai.standard.MultiTransformation;
import static com.tekai.Helpers.word;
import static com.tekai.Expression.e;

/**
 *
 * @author SFPISA
 */
public class TransformationConfig {

    /*
     * This class separates rules of TransformationRules in groups
     */
    private MultiTransformation tORACLE;
    private MultiTransformation tMSSQL;
    private MultiTransformation tMYSQL;
    private MultiTransformation tFIREBIRD;
    private MultiTransformation tPOSTGRES;

    private MultiTransformation tParam;

    private static Transformation t1 = from(word("SUBSTR"), "FUNCTION").toValue("SUBSTRING");
    private static Transformation t3 = from(word("POSITION"), "FUNCTION-POSITION").toValue("CHARINDEX").toType("FUNCTION");
    private static Transformation t4 = from(word("POSITION"), "FUNCTION-POSITION").toValue("INSTR").toType("FUNCTION").toParamOrder(2, 1);
    private static Transformation t5 = from("\\|\\|", "CONCAT").toValue("+");
    private static Transformation t6 = from("\\|\\|", "CONCAT").toValue("CONCAT").toType("FUNCTION");
    private static Transformation t7 = from(word("CHAR_LENGTH"), "FUNCTION").toValue("LEN");
    private static Transformation t8 = from(word("CHAR_LENGTH"), "FUNCTION").toValue("LENGTH");
    private static Transformation t9 = from(word("END"), "END").toValue("END CASE");
    private static Transformation t10 = new Transformation() {
        @Override
        public boolean when(Expression expression) {
            return expression.hasValue(word("TRIM")) && expression.isType("FUNCTION");}
        @Override
        public Expression then(String value, String type, List<Expression> children) {
            return e(" LTRIM", "FUNCTION",e("RTRIM", "FUNCTION", children));
        }};
    private static Transformation t11 = from(word("SUBSTR"), "FUNCTION").toValue("SUBSTRING").toType("SUBSTRING-FIREBIRD");
    private static Transformation t12 = fromType("PARAMETER").toValue("NULL");
    //Data Types
    private static Transformation t13 = fromValue(word("INTEGER")).toValue("INT");
    private static Transformation t14 = fromValue(word("INTEGER")).toValue("NUMBER(10)");
    private static Transformation t15 = fromValue(word("BIGINT")).toValue("NUMBER(19)");
    private static Transformation t16 = from(word("DECIMAL"),"FUNCTION").toValue("NUMBER");
    private static Transformation t17 = fromValue(word("VARCHAR")).toValue("VARCHAR2");
    private static Transformation t18 = fromValue(word("TEXT")).toValue("CLOB");
    private static Transformation t19 = fromValue(word("TEXT")).toValue("LONGTEXT");
    private static Transformation t20 = fromValue(word("TEXT")).toValue("BLOB  SUB_TYPE 1");
    private static Transformation t21 = fromValue(word("TIMESTAMP")).toValue("DATETIME");
    private static Transformation t22 = fromValue(word("TIMESTAMP")).toValue("DATE");
    private static Transformation t23 = fromValue(word("BYTEA")).toValue("IMAGE");
    private static Transformation t24 = fromValue(word("BYTEA")).toValue("BLOB");
    private static Transformation t25 = fromValue(word("BYTEA")).toValue("LONGBLOB");
    //RETIRA ALIAS PARA ORACLE
    private static Transformation t26 = fromType("ALIAS").toValue("");
    //CORRIGE TIPOS DE DADOS NO CAST DO MYSQL
    private static Transformation t27 = new Transformation() {
        @Override
        public boolean when(Expression expression) {
            return expression.hasValue(word("CAST")) && expression.isType("FUNCTION");}
        @Override
        public Expression then(String value, String type, List<Expression> children) {
            Expression alias = children.get(0);
            List<Expression> childrens = alias.getChildren();
            Expression tipo = childrens.get(1);
            if(tipo.hasValue(word("INTEGER")) || tipo.hasValue(word("BIGINT")) || tipo.hasValue(word("INT")))
                tipo = new Expression(tipo.getType(), tipo.getSpacing()+"SIGNED");
            else if(tipo.hasValue("VARCHAR"))
                tipo = new Expression(tipo.getType(), tipo.getSpacing()+"CHAR");
            childrens.set(1, tipo);
            alias.addChildren(childrens);

            return e(value, type, alias);
        }};
    //VAZIO PARA NULL - ORACLE
    private static Transformation t28 = from("''", "STRING").toValue("NULL").toType("IDENTIFIER");
    private static Transformation t29 = new Transformation() {
        @Override
        public boolean when(Expression expression) {
            return expression.hasValue("=") || expression.hasValue("!=")
                    || expression.hasValue("<>") && expression.isType("OPERATOR"); }
        @Override
        public Expression then(String value, String type, List<Expression> children) {
            if(children.get(1).hasValue("''") || children.get(1).hasValue("NULL")){
                type = "IS";
                if(value.trim().equals("=")) value = " IS";
                else value = " IS NOT"; }
            return e(value, type, children);
    }};

    public TransformationConfig(){
        tORACLE = new MultiTransformation();
        tMSSQL = new MultiTransformation();
        tMYSQL = new MultiTransformation();
        tFIREBIRD = new MultiTransformation();
        tPOSTGRES = new MultiTransformation();
        tParam = new MultiTransformation();

        tORACLE.register(t4)
               .register(t8)
               .register(t14)
               .register(t15)
               .register(t16)
               .register(t17)
               .register(t18)
               .register(t22)
               .register(t24)
               .register(t26)
               .register(t28)
               .register(t29);

        tMSSQL.register(t1)
              .register(t3)
              .register(t5)
              .register(t7)
              .register(t10)
              .register(t13)
              .register(t21)
              .register(t23);

        tMYSQL.register(t6)
              .register(t13)
              .register(t19)
              .register(t21)
              .register(t25)
              .register(t27);

        tFIREBIRD.register(t11)
                .register(t20)
                .register(t24);

        tParam.register(t12);
    }

    public MultiTransformation gettFIREBIRD() {
        return tFIREBIRD;
    }

    public MultiTransformation gettMSSQL() {
        return tMSSQL;
    }

    public MultiTransformation gettMYSQL() {
        return tMYSQL;
    }

    public MultiTransformation gettORACLE() {
        return tORACLE;
    }

    public MultiTransformation gettPOSTGRES() {
        return tPOSTGRES;
    }

    public MultiTransformation gettParam() {
        return tParam;
    }


}
