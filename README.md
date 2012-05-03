# Treck-o

Command line utility for querying Trello for common data.

Currently only has options for querying cards based on due date.

More to come.

## Install

```bash
lein deps
lein uberjar
```

## Usage

You need to set ENV variables for your Trello auth key and token. The two environment vars to set are

```bash
export TRELLO_KEY="mykey"
export TRELLO_TOKEN="mytoken"
```

### Getting KEY and TOKEN for API access

* Public Key: [visit this link while logged in Trello](https://trello.com/1/appKey/generate).

* With these values [visit this other link](https://trello.com/1/connect?key=<PUBLIC_KEY>&name=MyApp&response_type=token) (Replacing, of course &lt;PUBLIC_KEY&gt; for the public key value obtained).
** If you need read/write access, add the following to the end of the prior URL:  &scope=read,write

* Authorice MyApp to read the application

List overdue cards
```bash
java -jar trello-get-0.1.0-SNAPSHOT-standalone.jar -o 
```

List cards due tomorrow
```bash
java -jar trello-get-0.1.0-SNAPSHOT-standalone.jar -T 
```

## License

Copyright © 2012 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
