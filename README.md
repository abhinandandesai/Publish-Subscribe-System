## Publish-Subscribe System


### Launching docker containers for the RMI example
To start rmiserver and rmiclient and rebuild the docker image
```bash
docker-compose up --build rmiserver rmiclient 
```
After awhile you should see the following output
```bash
...
...
Successfully tagged csci652:latest
Recreating peer1 ... done
Recreating rmiserver ... done
Recreating rmiclient ... done
Attaching to rmiserver, rmiclient
rmiserver    | Initialize rmiserver...done!
rmiclient    | Initialize rmiclient...done!
```

### Start RMI server
Attach to the rmiserver container
```bash
docker exec -it rmiserver bash
```

Run the rmiserver program
```bash
java -Xmx2048m -cp target/project2-1.0-SNAPSHOT.jar -Djava.security.policy=rmi.policy edu.rit.cs.Server
```

Expected output
```bash
java RMI registry created.
Event Manager object created.
Rebind Complete.
Event Manager bound in registry at rmiserver:1099
What would you like to do?
 1: Display Topics.
 2: Display Subscribers.
 3: Quit Server.
Enter an Option: 
```

### Start RMI client
Attach to the rmiclient container
```bash
docker exec -it rmiclient bash
```

There are two States of a client or agent:

If it is a new agent or client then -
```bash
java -Xmx2048m -cp target/project2-1.0-SNAPSHOT.jar -Djava.security.policy=rmi.policy edu.rit.cs.Client new
```


If the client is trying to re-connect then - 
```bash
java -Xmx2048m -cp target/project2-1.0-SNAPSHOT.jar -Djava.security.policy=rmi.policy edu.rit.cs.Client -load
```

Expected output
```bash
Select one of the following options:
 1: Be a publisher.
 2: Be a subscriber.
 3: Save & Quit.
Enter a number:
```


