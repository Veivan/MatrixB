package main;

import inrtfs.IUser;

import java.util.List;

public class Brain {
    private List<IUser> accounts;

    public MatrixAct getAction(){
    	MatrixAct act = new MatrixAct(1, "act1");
    	return act;
    };
}
