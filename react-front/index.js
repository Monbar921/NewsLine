import React from "react";
import ReactDOM from "react-dom";
import Service from "./components/service/Service.js";
import 'bootstrap/dist/css/bootstrap.css';
import {BrowserRouter as Router} from "react-router-dom";

const appRouting = (
    <Router><Service/></Router>
);

ReactDOM.render(appRouting, document.getElementById("root"));
