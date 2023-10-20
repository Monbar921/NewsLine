import React, {useState} from 'react';
import {
    MDBContainer,
    MDBRow,
    MDBCol,
    MDBCard,
    MDBCardBody,
    MDBCardImage,
    MDBInput,
}
    from 'mdb-react-ui-kit';

function SignUp({handler}) {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [repeatPassword, setRepeatPassword] = useState('');
    const MIN_PASSWORD_LENGTH = 6;

    const handleSubmit = () => {
        if (password === repeatPassword) {
            if (validatePassword(password)) {
                if (validateEmail(email)) {
                    const body = {
                        username: username,
                        email: email,
                        password: password
                    }
                    handler(body)
                } else {
                    alert("Give me correct email")
                }
            } else {
                alert("Password can`t be less than 6 characters")
            }


        } else {
            alert("Provide equal passwords in corresponding fields")
        }
    };

    const validateEmail = (email) => {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    };

    const validatePassword = (password) => {
        return password !== undefined && password.trim().length >= 6
    }

    return (
        <div>
            <MDBContainer fluid>

                <MDBCard className='text-black m-5' style={{borderRadius: '25px'}}>
                    <MDBCardBody>
                        <MDBRow>
                            <MDBCol md='10' lg='6' className='order-2 order-lg-1 d-flex flex-column align-items-center'>

                                <p className="text-center h1 fw-bold mb-5 mx-1 mx-md-4 mt-4">Sign up</p>

                                <div className="d-flex flex-row align-items-center mb-4 ">
                                    <MDBInput label='Your Username' id='form1' type='text' className='w-100'
                                              required={true}
                                              onChange={e => setUsername(e.target.value)}/>
                                </div>

                                <div className="d-flex flex-row align-items-center mb-4">
                                    <MDBInput label='Your Email' id='form2' type='email' required={true}
                                              onChange={e => setEmail(e.target.value)}/>
                                </div>

                                <div className="d-flex flex-row align-items-center mb-4">
                                    <MDBInput label='Password' id='form3' type='password' required={true}
                                              onChange={e => setPassword(e.target.value)}/>
                                </div>

                                <div className="d-flex flex-row align-items-center mb-4">
                                    <MDBInput label='Repeat your password' id='form4' type='password' required={true}
                                              onChange={e => setRepeatPassword(e.target.value)}/>
                                </div>
                                <button type="button" className="btn btn-primary" onClick={handleSubmit}
                                        disabled={username === '' || email === '' || password === '' ||
                                            repeatPassword === '' || password.length < MIN_PASSWORD_LENGTH}>Register
                                </button>
                            </MDBCol>

                            {/*<MDBCol md='10' lg='6' className='order-1 order-lg-2 d-flex align-items-center'>*/}
                            {/*    <MDBCardImage*/}
                            {/*        src='/mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-registration/draw1.webp'*/}
                            {/*        fluid/>*/}
                            {/*</MDBCol>*/}

                        </MDBRow>
                    </MDBCardBody>

                    <div className="d-flex justify-content-end">
                        <button type="button" className="btn btn-primary" onClick={() => window.location.replace("/signin")}>
                            Login
                        </button>
                    </div>

                </MDBCard>

            </MDBContainer>
        </div>
    );
}

export default SignUp;
