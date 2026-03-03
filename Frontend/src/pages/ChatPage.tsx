import { useNavigate } from "react-router-dom";
import { Header } from "../components/Header";
import { Footer } from "../components/Footer";
import "../styles/ChatPage.css";

export const ChatPage = () => {
  const navigate = useNavigate();

  return (
    <div className="chat-page">
      <Header onNavigateToLogin={() => navigate("/login")} />
      <main className="chat-main" />
      <Footer />
    </div>
  );
};
