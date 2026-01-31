package org.SewageRMI.implementations;

import interfaces.ITailor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;


public class Tailor implements ITailor {
    Map<String, Remote> map = new HashMap<>();

    @Override
    public boolean register(String name, Remote r) throws RemoteException {
        map.put(name, r);
        System.out.println("registered " + name);
        return true;
    }

    @Override
    public boolean unregister(String name) throws RemoteException {
        map.remove(name);
        return true;
    }

    @Override
    public Remote getRemote(String name) throws RemoteException {
        return map.get(name);
    }

    public static void main(String[] args) {
        try {
            int port = args.length > 0 ? Integer.parseInt(args[0]) : 1099;
            Tailor tailor = new Tailor();
            ITailor it = (ITailor) UnicastRemoteObject.exportObject(tailor, 0);
            Registry r = LocateRegistry.createRegistry(port);
            r.rebind("Tailor", it);
            System.out.println("Tailor (RMI Registry) is running on port " + port);
            Thread.sleep(Long.MAX_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}