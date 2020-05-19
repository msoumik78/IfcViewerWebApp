package util;

import org.apache.log4j.Logger;

public class AuditLogger {

    static final Logger LOGGER = Logger.getLogger("audit");
    public static void auditLog(String userName, String action, String outcome){
        LOGGER.info(","+userName+","+action+","+outcome);
    }
}
