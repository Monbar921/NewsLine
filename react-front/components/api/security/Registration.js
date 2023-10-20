const registerURL = '/auth-server/oauth2/register';
// const registerURL = 'http://auth-server:9000/oauth2/register';
export const registration = (body) => {
    const response = fetch(registerURL, {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
        },
        mode: "cors"
    }).then(response => {
        console.log(response)
        console.log(response.status)
        if (response.status < 200 || response.status >= 300) {
            alert("Problem with registration");
        } else {
            window.location.replace("/signin");
        }
    })
        .catch(e => alert("Something went wrong"));

}