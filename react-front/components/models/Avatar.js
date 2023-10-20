import React from "react";
import {MDBCardImage} from "mdb-react-ui-kit";
import deleteIcon from "../../recources/delete.png";


const Avatar = ({URL}) => {
    return (
            <MDBCardImage
                className="rounded-circle shadow-1-strong me-3"
                src={URL}
                alt="avatar"
                width="65"
                height="65"
            />

    );
}

export default Avatar;
