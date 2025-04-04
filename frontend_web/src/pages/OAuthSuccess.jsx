import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const OAuthSuccess = () => {
  const navigate = useNavigate();
  useEffect(() => {
    const token = new URLSearchParams(window.location.search).get("token");

    if (token) {
        console.log("sucess? asd1")
      localStorage.setItem("token", token); 
      navigate("/dashboard"); 
    // window.location.href = "/dashboard";
    }else{
        console.log("errorr nganu")

    }
  }, [navigate]);

  return <div>Authenticating... Please wait.</div>;
};

export default OAuthSuccess;