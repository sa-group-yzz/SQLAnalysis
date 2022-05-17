package top.viewv;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SQLAnalysis {
    private SQLStatement statement;
    private SQLStatementParser parser;
    private MySqlSchemaStatVisitor visitor;

    public SQLAnalysis(String sql) {
        parser = new SQLStatementParser(sql);
        statement = parser.parseStatement();
        visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);
    }

    public Set<TableStat.Name> getTables() {
        return visitor.getTables().keySet();
    }

    public List<TableStat.Condition> getConditions() {
        return visitor.getConditions();
    }

    public Collection<TableStat.Column> getColumns() {
        return visitor.getColumns();
    }
}
