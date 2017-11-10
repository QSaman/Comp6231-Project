package comp6231.project.mostafa.core;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import clientSide.UserInterface;


public class Program {

	private static NamingContextExt ncRef;

	public static void main(String[] args) {
		ORB orb = ORB.init(args,null);
		try {
			Object objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
		} catch (InvalidName invalidName) {
			invalidName.printStackTrace();
		}
		new UserInterface();
	}

	/**
	 * @return the ncRef
	 */
	public static NamingContextExt getNcRef() {
		return ncRef;
	}
}
