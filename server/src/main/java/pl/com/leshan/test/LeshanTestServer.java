package pl.com.leshan.test;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;

import java.util.Collection;


public class LeshanTestServer
{
    public static void main( String[] args ) {
        LeshanServerBuilder builder = new LeshanServerBuilder();
        LeshanServer server = builder.build();
        server.start();

        server.getRegistrationService().addListener(new RegistrationListener() {

            public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
                System.out.println("device is still here: " + updatedReg.getEndpoint());
            }

            public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
                                     Registration newReg) {
                System.out.println("device left: " + registration.getEndpoint());
            }

            @Override
            public void registered(Registration registration, Registration previousReg,
                                   Collection<Observation> previousObservations) {
                System.out.println("New device: " + registration.getEndpoint());

                ReadRequestHandler requestHandler = new ReadRequestHandler(server);
                requestHandler.startInteractiveReadLoop(registration);
            }
        });
    }
}