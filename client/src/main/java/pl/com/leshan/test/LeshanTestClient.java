package pl.com.leshan.test;

import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.leshan.client.LeshanClient;
import org.eclipse.leshan.client.LeshanClientBuilder;
import org.eclipse.leshan.client.californium.endpoint.CaliforniumClientEndpointsProvider;
import org.eclipse.leshan.client.californium.endpoint.coap.CoapClientProtocolProvider;
import org.eclipse.leshan.client.californium.endpoint.coaps.CoapsClientProtocolProvider;
import org.eclipse.leshan.client.object.Security;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.LwM2mId;
import org.eclipse.leshan.core.request.BindingMode;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumSet;

public class LeshanTestClient
{
    public static void main( String[] args ) {
        LeshanClientBuilder builder = new LeshanClientBuilder("myclient");

        CaliforniumClientEndpointsProvider.Builder endpointsBuilder = new CaliforniumClientEndpointsProvider.Builder(
                new CoapClientProtocolProvider(), new CoapsClientProtocolProvider());

        Configuration californiumConfig = endpointsBuilder.createDefaultConfiguration();
        californiumConfig.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, true);
        endpointsBuilder.setConfiguration(californiumConfig);

        try {
            endpointsBuilder.setClientAddress(InetAddress.getByName("127.0.0.1"));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        builder.setEndpointsProviders(endpointsBuilder.build());
        ObjectsInitializer initializer = new ObjectsInitializer();

        initializer.setInstancesForObject(LwM2mId.SECURITY, Security.noSec("coap://127.0.0.1:5683", 12345));
        initializer.setInstancesForObject(LwM2mId.SERVER, new Server(12345, 5*60, EnumSet.of(BindingMode.U), false, BindingMode.U));
        initializer.setInstancesForObject(LwM2mId.DEVICE, new TestDevice());

        builder.setObjects(initializer.createAll());
        LeshanClient client = builder.build();

        client.start();

    }
}
