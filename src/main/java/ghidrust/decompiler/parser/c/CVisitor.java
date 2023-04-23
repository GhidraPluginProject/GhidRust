package ghidrust.decompiler.parser.c;

import ghidrust.decompiler.parser.c.gen.*;
import java.util.HashMap;

/* Generated By:JavaCC: Do not edit this line. CParserDefaultVisitor.java Version 7.0.9 */
public class CVisitor implements CParserVisitor {
    HashMap<String, String> type_map = new HashMap<String, String>();

    int indent_level = 0;

    public CVisitor() {
        type_map.put("void", "");
        type_map.put("int", "i32");

        /* Not entirely true, but works for now */
        type_map.put("char", "char");

        type_map.put("short", "i16");
        type_map.put("long", "i32");
        type_map.put("float", "f32");
        type_map.put("double", "f64");
        type_map.put("signed", "i32");
        type_map.put("unsigned", "u32");
        type_map.put("code", "code");
    }

    private StringBuilder indent(StringBuilder sb) {
        for (int i = 0; i < indent_level; i++) {
            sb.append("\t");
        }

        return sb;
    }

    public Object defaultVisit(SimpleNode node, Object data) {
        StringBuilder sb = new StringBuilder("");

        int child_count = node.jjtGetNumChildren();
        for (int i = 0; i < child_count; i++) {
            Node child = node.jjtGetChild(i);
            String ret = (String) child.jjtAccept(this, data);
            if (ret != null) {
                sb.append(ret);
            }
        }

        return sb.toString();
    }

    public Object defaultSpacedVisit(SimpleNode node, Object data, String separator, boolean last) {
        StringBuilder sb = new StringBuilder("");

        int child_count = node.jjtGetNumChildren();
        for (int i = 0; i < child_count; i++) {
            Node child = node.jjtGetChild(i);
            String ret = (String) child.jjtAccept(this, data);
            if (ret != null) {
                sb.append(ret);
                if (!ret.equals("") && (last || i != child_count - 1)) {
                    sb.append(separator);
                }
            }
        }

        return sb.toString();
    }

    public Object visit(SimpleNode node, Object data) {
        return node.jjtAccept(this, data);
    }

    public Object visit(ASTStringToken node, Object data) {
        return node.getValue();
    }

    public Object visit(ASTGhostStringToken node, Object data) {
        return "";
    }

    public Object visit(ASTTypeStringToken node, Object data) {
        String typename = node.getValue();

        if (type_map.containsKey(typename)) {
            return type_map.get(typename);
        } else {
            return typename;
        }
    }

    public Object visit(ASTFunctionDefinition node, Object data) {
        node.dump("");
        StringBuilder rust_code = new StringBuilder("");

        rust_code.append("fn ");
        rust_code.append(node.jjtGetChild(1).jjtAccept(this, data));

        String ret_type = (String) node.jjtGetChild(0).jjtAccept(this, data);
        if (!ret_type.equals("")) {
            rust_code.append("-> ");
        }
        rust_code.append(ret_type);
        rust_code.append(" {\n");
        indent_level++;
        rust_code.append(node.jjtGetChild(2).jjtAccept(this, data));
        rust_code.append("}\n");
        indent_level--;

        return rust_code.toString();
    }

    public Object visit(ASTDeclaration node, Object data) {
        StringBuilder sb = new StringBuilder("");
        String[] ret = (String[]) node.jjtGetChild(1).jjtAccept(this, data);

        for (int i = 0; i < ret.length / 2; i++) {
            if (i != 0) {
                sb = indent(sb);
            }

            sb.append("let mut ");
            sb.append(ret[2 * i]);
            sb.append(": ");
            sb.append(node.jjtGetChild(0).jjtAccept(this, data));
            if (ret[2 * i + 1] != null) {
                sb.append(" = ");
                sb.append(ret[2 * i + 1]);
            }
            sb.append(";");
            sb.append("\n");
        }

        return sb.toString();
    }

    public Object visit(ASTDeclarationList node, Object data) {
        StringBuilder sb = indent(new StringBuilder(""));
        return sb.toString() + defaultSpacedVisit(node, data, sb.toString(), false);
    }

    public Object visit(ASTDeclarationSpecifiers node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTInitDeclaratorList node, Object data) {
        String[] ret = new String[node.jjtGetNumChildren() * 2];
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            String[] child_ret = (String[]) node.jjtGetChild(i).jjtAccept(this, data);
            ret[2 * i] = child_ret[0];
            ret[2 * i + 1] = child_ret[1];
        }

        return ret;
    }

    public Object visit(ASTInitDeclarator node, Object data) {
        String[] ret = new String[2];
        ret[0] = (String) node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            ret[1] = null;
        } else {
            ret[1] = (String) node.jjtGetChild(1).jjtAccept(this, data);
        }

        return ret;
    }

    public Object visit(ASTSpecifierQualifierList node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTDeclarator node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTDirectDeclarator node, Object data) {
        int child_num = node.jjtGetNumChildren();

        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < child_num; i++) {
            Node child = node.jjtGetChild(i);
            String child_val = (String) child.jjtAccept(this, data);

            if (child instanceof ASTDeclarator || child instanceof ASTParameterTypeList
                    || child instanceof ASTIdentifierList) {
                sb.append("(");
                sb.append(child_val);
                sb.append(") ");
            } else if (child instanceof ASTConstantExpression) {
                sb.append("[");
                sb.append(child_val);
                sb.append("] ");
            } else {
                sb.append(child_val);
            }
        }

        return sb.toString();
    }

    public Object visit(ASTPointer node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTTypeQualifierList node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTParameterTypeList node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTParameterList node, Object data) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(node.jjtGetChild(i).jjtAccept(this, data));
        }

        return sb.toString();
    }

    public Object visit(ASTParameterDeclaration node, Object data) {
        StringBuilder sb = new StringBuilder("");

        if (node.jjtGetNumChildren() > 1) {
            sb.append(node.jjtGetChild(1).jjtAccept(this, data));
            sb.append(": ");
        }
        sb.append(node.jjtGetChild(0).jjtAccept(this, data));
        return sb.toString();
    }

    public Object visit(ASTIdentifierList node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTInitializer node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTInitializerList node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTTypeName node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTAbstractDeclarator node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTDirectAbstractDeclarator node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTStatement node, Object data) {
        StringBuilder sb = indent(new StringBuilder(""));
        return sb.toString() + defaultVisit(node, data);
    }

    public Object visit(ASTLabeledStatement node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTExpressionStatement node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTCompoundStatement node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTStatementList node, Object data) {
        return defaultSpacedVisit(node, data, "\n", true);
    }

    public Object visit(ASTSelectionStatement node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTIterationStatement node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTJumpStatement node, Object data) {
        StringBuilder sb = new StringBuilder("");

        if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0) instanceof ASTExpression) {
            sb.append("return ");
            sb.append(node.jjtGetChild(0).jjtAccept(this, data));
        } else {
            sb.append(defaultVisit(node, data));
        }
        return sb.toString();
    }

    public Object visit(ASTExpression node, Object data) {
        if (node.jjtGetChild(0) instanceof ASTAssignmentExpression) {
            return defaultSpacedVisit(node, data, " ", false) + ";";
        } else {
            ASTDeclaration decl = new ASTDeclaration(0);
            decl.jjtAddChild(node.jjtGetChild(0), 0);
            decl.jjtAddChild(node.jjtGetChild(1), 1);
            return decl.jjtAccept(this, data);
        }
    }

    public Object visit(ASTAssignmentExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTConditionalExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTConstantExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTLogicalORExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTLogicalANDExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTInclusiveORExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTExclusiveORExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTANDExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTEqualityExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTRelationalExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTShiftExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTAdditiveExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTMultiplicativeExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTCastExpression node, Object data) {
        StringBuilder sb = new StringBuilder("");
        if (node.jjtGetNumChildren() > 1) {
            sb.append(node.jjtGetChild(1).jjtAccept(this, data));
            sb.append(" as ");
        }
        sb.append(node.jjtGetChild(0).jjtAccept(this, data));

        return sb.toString();
    }

    public Object visit(ASTUnaryExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTUnaryOperator node, Object data) {
        return defaultVisit(node, data);
    }

    public Object visit(ASTPostfixExpression node, Object data) {
        StringBuilder sb = new StringBuilder("");
        sb.append(node.jjtGetChild(0).jjtAccept(this, data));

        if (node.choice == 2) {
            /* Function call */
            sb.append("(");

            for (int i = 1; i < node.jjtGetNumChildren(); i++) {
                sb.append(node.jjtGetChild(i).jjtAccept(this, data));
            }

            sb.append(")");
            return sb.toString();
        }

        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTPrimaryExpression node, Object data) {
        return defaultSpacedVisit(node, data, " ", false);
    }

    public Object visit(ASTArgumentExpressionList node, Object data) {
        return defaultSpacedVisit(node, data, ", ", false);
    }
}
/*
 * JavaCC - OriginalChecksum=fd39d82df2a1b516298b94d6f4a5e997 (do not edit this
 * line)
 */
