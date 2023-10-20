import React, {useState} from 'react';

function SignInPage({handler}) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleUsernameChange = (event) => {
        setUsername(event.target.value);
    };

    const handlePasswordChange = (event) => {
        setPassword(event.target.value);
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        handler(event);
    };

    return (
        <div>
            <h2>Login Page</h2>
            <button type="submit" onClick={handleSubmit}>Sign In</button>
            <br/>
            <a href="/signup">Sign Up</a>
        </div>
    );
}

export default SignInPage;
