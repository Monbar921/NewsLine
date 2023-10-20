import React, {useState} from "react";
import Cookies from 'js-cookie';
import {
    MDBBtn,
    MDBCard,
    MDBCardBody,
    MDBTextArea,
    MDBTypography,
} from "mdb-react-ui-kit";


const LeaveCommentForm = ({newsId, replyToUsername, replyToCommentId, saveComment}) => {
    const [text, setText] = useState('');
    const handleSend = () => {
        const comment = {
            newsId: newsId,
            text: text,
            email: Cookies.get("email"),
            parentComment: replyToCommentId,
            createdAt: Date.now()
        }

        saveComment(newsId, comment)
    }
    return (
        <MDBCard>
            <MDBCardBody className="p-4">
                <div className="d-flex flex-start w-100">

                    <div className="w-100">
                        <div
                            className="d-flex justify-content-between align-items-center">
                            <MDBTypography tag="h5">Add a comment</MDBTypography>
                            <div>
                                {replyToUsername.length !== 0 &&
                                    <label>reply to: {replyToUsername}</label>}
                            </div>
                        </div>


                        <MDBTextArea label="What is your view?" rows={4} onChange={e => setText(e.target.value)}/>

                        <button type="button" className="btn btn-primary" onClick={handleSend}
                                disabled={text.length === 0}>Send
                        </button>
                    </div>
                </div>
            </MDBCardBody>
        </MDBCard>
    );
}

export default LeaveCommentForm;