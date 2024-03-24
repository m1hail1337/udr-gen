package ru.nxbootcamp.semenov.cdr.db;

public abstract class JdbcRepository {

    protected String JDBC_URL = "jdbc:h2:~/test;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE SCHEMA IF NOT EXISTS cdr\\;SET SCHEMA cdr";
    protected String username = "sa";
    protected String password = "1234";

    protected abstract void initRepository() throws ClassNotFoundException;
}
