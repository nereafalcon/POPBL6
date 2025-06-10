package com.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IUser extends Remote {
    void notify(String message) throws RemoteException;
}