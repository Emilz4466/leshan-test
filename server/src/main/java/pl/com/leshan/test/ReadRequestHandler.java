package pl.com.leshan.test;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ReadRequestHandler {
    private final LeshanServer server;

    public ReadRequestHandler(LeshanServer server) {
        this.server = server;
    }

    public void startInteractiveReadLoop(Registration registration) {
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            System.out.println("Enter resource ID (0-22) to read, or 'q' to quit:");
            input = scanner.nextLine();

            if ("q".equalsIgnoreCase(input)) {
                System.out.println("Exiting read loop...");
                break;
            }

            try {
                int resourceId = Integer.parseInt(input);
                if (resourceId < 0 || resourceId > 22) {
                    System.out.println("Invalid resource ID. Please enter a number between 0 and 22.");
                    continue;
                }

                System.out.println("Sending ReadRequest for resource ID: " + resourceId);
                ReadResponse response = server.send(registration, new ReadRequest(3, 0, resourceId));

                if (response.isSuccess()) {
                    Object value = ((LwM2mResource) response.getContent()).getValue();
                    if (value instanceof byte[]) {
                        String stringValue = new String((byte[]) value, StandardCharsets.UTF_8);
                        System.out.println("Response (as String): " + stringValue);
                    } else {
                        System.out.println("Response: " + value);
                    }
                } else {
                    System.out.println("Failed to read: " + response.getCode() + " " + response.getErrorMessage());
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number or 'q' to quit.");
            } catch (InterruptedException e) {
                System.err.println("Error during ReadRequest: " + e.getMessage());
            }
        } while (true);

        scanner.close();
    }
}
