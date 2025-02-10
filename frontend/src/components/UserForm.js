import { useState } from "react";

const App = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isLogin, setIsLogin] = useState(true);
    const [message, setMessage] = useState("");

    const checkUserExists = async () => {
        const response = await fetch(`http://localhost:8080/api/users/${username}`);
        const exists = await response.json();
        setMessage(exists ? "Username exists" : "Username available");
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        const response = await fetch("http://localhost:8080/api/users/handle", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
                username,
                password,
                isLogin
            }),
        });

        const user = await response.json();
        setMessage(user ? "Success!" : "Error logging in/registering");
    };

    return (
        <div>
            <h1>User Authentication</h1>
            <input
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                onBlur={checkUserExists} // Trigger GET request
            />
            <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <button onClick={() => setIsLogin(true)}>Login</button>
            <button onClick={() => setIsLogin(false)}>Register</button>
            <button onClick={handleSubmit}>Submit</button>
            <p>{message}</p>
        </div>
    );
};

export default App;
