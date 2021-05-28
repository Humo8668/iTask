package uz.app.iTask.Util;

public class Query {
    enum State {
        Empty,                  // Initial state of query
        Completed,              // SQL query can be executed
        ColumnsExp,             // Expecting columns
        TableNameExp,           // Expecting table or view name
        FilterExp,              // Expecting <WHERE> clause, filter condition
        OrderExp,               // For <SELECT> statement. Expecting order (ASC or DESC)
        ColumnNameExp,          // For <UPDATE> statement. Expecting column name for update
        ColumnValueExp,         //
    }

    String sqlQueryType = "";

    // FOR SELECT QUERY
    String FROM = "";
    String WHERE = "";
    String ORDERBY = "";
    int OFFSET = 0;
    int LIMIT = -1;

}
