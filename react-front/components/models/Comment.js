import React, {useState} from "react";
import {MDBCardImage, MDBIcon, MDBTextArea} from "mdb-react-ui-kit";
import Avatar from "./Avatar";
import img from "../../recources/user.png";
import deleteIcon from "../../recources/delete.png";
import Popup from "reactjs-popup";


const Comment = ({data, setReplyToUsername, setReplyToCommentId, updateComment, removeComment}) => {
    const [text, setText] = useState(data.text);
    const date = new Date(data.createdAt)
    const formattedDate = date.getDate() + "-" + (date.getMonth() + 1) + "-" + date.getFullYear() + "  " + date.getHours() + ":" + date.getMinutes();
    const [changedText, setChangedText] = useState(data.text);

    const handleChangeComment = () => {
        console.log("update comment")
        if (changedText !== text) {
            setText(changedText)
            data.text = changedText;
            console.log(data)
            updateComment(data.newsId, data.id, data)
        }
        console.log(changedText);
    }

    return (
        <div className={"d-flex flex-start"}>
            <div>
                <a href="javascript:void(0)" onClick={() => {
                    if (data.deleted === false) {
                        removeComment(data.newsId, data.id)
                    }

                }}>
                    <MDBCardImage
                        className="rounded-circle shadow-1-strong me-3"
                        src={deleteIcon}
                        alt="avatar"
                        width="25"
                        height="25"
                    />
                </a>

                <Avatar URL={img}/>
            </div>

            <div className="flex-grow-1 flex-shrink-1">
                <div>
                    <div
                        className="d-flex justify-content-between align-items-center">
                        <div>
                            <p className="mb-1">
                                {data.username}
                                <span className="small">  - {formattedDate}</span>
                            </p>
                        </div>

                        <div className="ps-3  ">
                            <a href="javascript:void(0)" onClick={e => {
                                setReplyToUsername(data.username);
                                setReplyToCommentId(data.id);
                            }
                            }>
                                <MDBIcon fas icon="reply fa-xs"/>
                                <span className="small"> reply</span>
                            </a>
                        </div>

                        <div className="ps-3 ">
                            <Popup
                                trigger={<button disabled={data.deleted }>Edit</button>}
                                modal
                                closeOnDocumentClick
                                disabled={data.deleted}


                            >
                                {(close) => (
                                    <div className="popup-content light">
                                        <h2>Edit comment</h2>
                                        <MDBTextArea rows={2} onChange={e => setChangedText(e.target.value)}
                                                     value={changedText}/>
                                        <button type="button" className="btn btn-primary" onClick={() => {
                                            handleChangeComment()
                                            close()
                                        }}
                                                disabled={changedText.length === 0}>
                                            Save
                                        </button>

                                    </div>
                                )}
                            </Popup>
                        </div>

                    </div>
                    {data.deleted === true &&
                        <p className="small mb-0 fst-italic">
                            {text}
                        </p>
                    }
                    {data.deleted === false &&
                        <p className="small mb-0">
                            {text}
                        </p>
                    }
                </div>
            </div>
        </div>


    )
        ;
}

export default Comment;
