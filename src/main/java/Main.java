import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.util.cnfexpression.MultiAndExpression;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        final String insertSQL = "INSERT INTO emp (empno, ename, job, sal, comm, deptno, joinedon) VALUES (4160, 'STURDEVIN', 'SECURITY GUARD', 2045, NULL, 30, TO_TIMESTAMP('2014-07-02 06:14:00.742000000', 'YYYY-MM-DD HH24:MI:SS.FF'));";
        final String deleteSQL = getDeleteSQLFromInsertSQL(insertSQL);
        System.out.println("insertSQL: " + insertSQL);
        System.out.println("deleteSQL: " + deleteSQL);
    }

    public static String getDeleteSQLFromInsertSQL(String insertSQL) {
        final Insert insertStatement = parseInsertSQL(insertSQL);
        final String deleteSQL = generateDeleteSQL(insertStatement);
        return deleteSQL;
    }

    private static Insert parseInsertSQL(final String insertSQL) {
        final Statement statement;
        Insert insertStatement = null;
        try {
            statement = CCJSqlParserUtil.parse(insertSQL);
            if (statement instanceof Insert) {
                insertStatement = (Insert) statement;
            }
        } catch (JSQLParserException jsqlParserException) {
            jsqlParserException.printStackTrace();
        }
        return insertStatement;
    }

    private static String generateDeleteSQL(final Insert insertStatement) {
        final List<Column> columns = insertStatement.getColumns();
        final ItemsList itemsList = insertStatement.getItemsList();
        final Delete deleteStatement = new Delete();
        deleteStatement.setTable(insertStatement.getTable());
        final MultiAndExpression whereExpression = generateWhereClause(columns, itemsList);
        deleteStatement.setWhere(whereExpression);
        return deleteStatement.toString();
    }

    private static MultiAndExpression generateWhereClause(final List<Column> columns, final ItemsList itemsList) {
        MultiAndExpression whereExpression = null;
        if (itemsList instanceof ExpressionList) {
            final ExpressionList expressionList = (ExpressionList) itemsList;
            final List<Expression> values = expressionList.getExpressions();
            List<Expression> expressions = new ArrayList<>();
            for (int counter = 0; counter < columns.size(); counter++) {
                EqualsTo equalsTo = new EqualsTo();
                equalsTo.setLeftExpression(columns.get(counter));
                equalsTo.setRightExpression(values.get(counter));
                expressions.add(equalsTo);
            }
            whereExpression = new MultiAndExpression(expressions);
        }
        return whereExpression;
    }

}
