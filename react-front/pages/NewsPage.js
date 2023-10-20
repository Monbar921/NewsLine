import React, {useState} from "react";
import {useLocation, useParams} from "react-router-dom";
import News from "../components/models/News";

import {
    MDBCard,
    MDBCardBody,
    MDBCol,
    MDBContainer,
    MDBRow,
    MDBTypography,
} from "mdb-react-ui-kit";
import Comment from "../components/models/Comment";
import LeaveCommentForm from "../components/models/LeaveCommentForm";

const NewsPage = ({getNews, getComments, drawImages, saveComment, updateComment, removeComment}) => {
    console.log("news page")
    const { newsId } = useParams();
    const [news, setNews] = useState({});
    const [downloadedNews, setDownloaded] = useState(false);
    getNews(newsId, setNews, setDownloaded);
    const [comments, setComments] = useState([]);
    const [downloadedComments, setDownloadedComments] = useState(false);
    getComments(newsId, setComments, setDownloadedComments);
    const [replyToUsername, setReplyToUsername] = useState('');
    const [replyToCommentId, setReplyToCommentId] = useState('');

    return (
        <div>
            {downloadedNews &&
                <section className="gradient-custom vh-100">
                    <News news={news} drawImages={drawImages} showButtons={false}/>
                    <br/>
                    <br/>

                    <MDBCol md="6" lg="6" xl="6">
                        {downloadedComments &&
                            <MDBCard>
                                <MDBCardBody className="p-4">
                                    <MDBTypography tag="h4" className="text-center mb-4 pb-2">
                                        Comments
                                    </MDBTypography>

                                    <LeaveCommentForm newsId={news.id} replyToUsername={replyToUsername}
                                                      replyToCommentId={replyToCommentId} saveComment={saveComment}/>
                                    <br/>

                                    <MDBRow>
                                        <MDBCol>

                                            <div className="flex-grow-1 flex-shrink-1">
                                                {comments.map((comment, index) => {
                                                    console.log(comment)

                                                    return (
                                                        <div key={index}
                                                             className={"d-flex flex-start"}
                                                             style={{
                                                                 marginLeft: (5 * 10 * comment.level + 'px')
                                                             }}>

                                                            <Comment data={comment}
                                                                     setReplyToUsername={setReplyToUsername}
                                                                     setReplyToCommentId={setReplyToCommentId}
                                                                     updateComment={updateComment}
                                                                     removeComment={removeComment}/>

                                                        </div>
                                                    );
                                                })}
                                            </div>

                                        </MDBCol>
                                    </MDBRow>
                                </MDBCardBody>
                            </MDBCard>
                        }
                    </MDBCol>
                </section>}
        </div>

    )
        ;
};

export default NewsPage;
