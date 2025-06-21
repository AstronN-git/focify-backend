### Usage
#### Build:
```sh
docker build -t focify .
``` 

#### Run:
```sh
docker run --env-file=.env -p8080:8080 focify
```
There should be `JWT_KEY=` variable in `.env`.  