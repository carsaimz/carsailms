import { useState } from "react";
import { MessageCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Chat } from "./Chat";
import { useAuth } from "@/contexts/AuthContext";

export const FloatingChatButton = () => {
  const [isOpen, setIsOpen] = useState(false);
  const { user } = useAuth();

  if (!user) return null;

  return (
    <>
      <Button
        onClick={() => setIsOpen(!isOpen)}
        className="fixed bottom-4 left-4 z-40 h-14 w-14 rounded-full shadow-lg"
        size="icon"
        variant="default"
      >
        <MessageCircle className="h-6 w-6" />
      </Button>
      <Chat isOpen={isOpen} onClose={() => setIsOpen(false)} />
    </>
  );
};
