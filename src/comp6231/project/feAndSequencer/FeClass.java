package comp6231.project.feAndSequencer;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import comp6231.project.common.corba.users.AdminOperations;
import comp6231.project.common.corba.users.AdminOperationsHelper;
import comp6231.project.common.corba.users.StudentOperations;
import comp6231.project.common.corba.users.StudentOperationsHelper;

public class FeClass {

	public static void main(String[] args) {
		
		Thread corbaThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ORB orb = ORB.init(args, null);
					
					POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
					rootpoa.the_POAManager().activate();
					
					POA rootpoa2 = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
					rootpoa2.the_POAManager().activate();
					
					StudentServant studentServant = new StudentServant();
					studentServant.setORB(orb);
					org.omg.CORBA.Object studentObject = rootpoa.servant_to_reference(studentServant);
					StudentOperations studentOperations = StudentOperationsHelper.narrow(studentObject);
					
					AdminServant adminServant = new AdminServant();
					adminServant.setORB(orb);
					org.omg.CORBA.Object adminObject = rootpoa2.servant_to_reference(adminServant);
					AdminOperations adminOperations = AdminOperationsHelper.narrow(adminObject);
					
					org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService"); 
					NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
					
					NameComponent path[] = ncRef.to_name( "FES" ); 
					ncRef.rebind(path, studentOperations); 
					
					org.omg.CORBA.Object objRef2 = orb.resolve_initial_references("NameService"); 
					NamingContextExt ncRef2 = NamingContextExtHelper.narrow(objRef2);
					
					NameComponent path2[] = ncRef2.to_name( "FEA" ); 
					ncRef2.rebind(path2, adminOperations); 
					
					System.out.println("|=== FE is ready ===|");
					
					while (true){
						orb.run(); 
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e);
				}				
			}
			
		});
		corbaThread.start();
	}
}
