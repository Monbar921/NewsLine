const express = require("express");
const app = express();
const path = require("path");
const port = process.env.PORT || 3000;
let cors = require('cors')
// app.use(cors())
app.use(cors({ credentials: true, origin: 'http://localhost:3000' }));
app.listen(port, () => {
    console.log(`The app server is running on port: ${port}`);
});

const DIST_DIR = path.join(__dirname, "dist");
const HTML_FILE = path.join(DIST_DIR, "index.html");

app.use(express.json());
app.use(express.static("public"));
app.use(express.static("dist"));

app.get("/", (req, res) => {
    res.sendFile(HTML_FILE, function (err) {
        if (err) {
            res.status(500).send(err);
        }
    });
});

app.get("/newsline", (req, res) => {
    res.sendFile(HTML_FILE, function (err) {
        if (err) {
            res.status(500).send(err);
        }
    });
});

app.get("/newsline/news/add-news", (req, res) => {
    res.sendFile(HTML_FILE, function (err) {
        if (err) {
            res.status(500).send(err);
        }
    });
});

app.get("/newsline/news/:newsId/edit-news", (req, res) => {
    res.sendFile(HTML_FILE, function (err) {
        if (err) {
            res.status(500).send(err);
        }
    });
});


app.get("/signup", (req, res) => {
    res.sendFile(HTML_FILE, function (err) {
        if (err) {
            res.status(500).send(err);
        }
    });
});

app.get("/signin", (req, res) => {
    res.sendFile(HTML_FILE, function (err) {
        if (err) {
            res.status(500).send(err);
        }
    });
});


app.get("/activation/**", (req, res) => {
    res.sendFile(HTML_FILE, function (err) {
        if (err) {
            res.status(500).send(err);
        }
    });
});


app.get("/authorized", (req, res) => {
    res.sendFile(HTML_FILE, function (err) {
        if (err) {
            res.status(500).send(err);
        }
    });
});

app.get("/newsline/news/:newsId", (req, res) => {
    res.sendFile(HTML_FILE, function (err) {
        if (err) {
            res.status(500).send(err);
        }
    });
});

