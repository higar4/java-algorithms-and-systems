module Sewage.System.RMI {

    requires sewagelib;

    requires java.rmi;
    requires java.desktop;
    requires java.logging;


    exports org.SewageRMI.implementations to java.rmi;
}