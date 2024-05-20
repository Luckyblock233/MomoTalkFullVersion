package all.database;

import all.config.Config;

import java.util.HashMap;
import java.util.Map;

public class ImportFactory {
    private static Map<String, UserImport> userImportMap = new HashMap<String, UserImport>();
    public static void initImport()
    {
        userImportMap.put("sqlite",new UserImportSqlite());
    }
    //	private static String DBTYPE = "oracle";
    public static UserImport getUserImport(){
        if (Config.DBTYPE.equals("sqlite")) {
            return new UserImportSqlite();
        } else if (Config.DBTYPE.equals("oracle")) {
           // return new UserImportOracle();
        } else if (Config.DBTYPE.equals("mysql")) {
            //return new UserImportMySql();
        }
        return null;

    }
}
