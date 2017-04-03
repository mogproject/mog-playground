### Dependencies

| Library       | Purpose      | URL  |
|:--------------|:-------------|:-----|
|ecl_new.js     |Japanese character set decoder|http://www.drk7.jp/MT/archives/001324.html |

### For developers

##### Development

```
npm install jsdom
sbt gen-idea  # workaround
```

##### Testing

```
sbt
```

- In the `sbt` console

```
> ~fastOptJS
```

- In another terminal

```
make local
```

##### Publishing

```
make publish
```

