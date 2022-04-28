package top.viewv;

public class Main {
    public static void main(String[] args) {
        String sql = "select * from user where id > 100 and id < 1000";
        SQLAnalysis sqlAnalysis = new SQLAnalysis(sql);
        System.out.println(sqlAnalysis.getTables());
        System.out.println(sqlAnalysis.getConditions());
        System.out.println(sqlAnalysis.getColumns());
    }
}
