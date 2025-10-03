import { useEffect, useState } from "react";
import { Bell } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useAuth } from "@/contexts/AuthContext";
import { db } from "@/lib/firebase";
import {
  collection,
  query,
  where,
  orderBy,
  onSnapshot,
  updateDoc,
  doc,
  limit,
} from "firebase/firestore";
import { useToast } from "@/hooks/use-toast";

interface Notification {
  id: string;
  userId: string;
  title: string;
  message: string;
  read: boolean;
  type: "info" | "success" | "warning" | "error";
  createdAt: any;
}

export const NotificationSystem = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const { user } = useAuth();
  const { toast } = useToast();

  useEffect(() => {
    if (!user) return;

    const q = query(
      collection(db, "notifications"),
      where("userId", "==", user.uid),
      orderBy("createdAt", "desc"),
      limit(20)
    );

    const unsubscribe = onSnapshot(
      q,
      (snapshot) => {
        const notifs: Notification[] = [];
        let unread = 0;

        snapshot.forEach((doc) => {
          const data = { id: doc.id, ...doc.data() } as Notification;
          notifs.push(data);
          if (!data.read) unread++;
        });

        setNotifications(notifs);
        setUnreadCount(unread);

        // Mostrar toast para novas notificações não lidas
        snapshot.docChanges().forEach((change) => {
          if (change.type === "added" && !change.doc.data().read) {
            const newNotif = change.doc.data();
            toast({
              title: newNotif.title,
              description: newNotif.message,
              variant: newNotif.type === "error" ? "destructive" : "default",
            });
          }
        });
      },
      (error) => {
        console.error("Erro ao carregar notificações:", error);
      }
    );

    return () => unsubscribe();
  }, [user, toast]);

  const markAsRead = async (notificationId: string) => {
    try {
      await updateDoc(doc(db, "notifications", notificationId), {
        read: true,
      });
    } catch (error) {
      console.error("Erro ao marcar notificação como lida:", error);
    }
  };

  const markAllAsRead = async () => {
    try {
      const unreadNotifs = notifications.filter((n) => !n.read);
      await Promise.all(
        unreadNotifs.map((n) =>
          updateDoc(doc(db, "notifications", n.id), { read: true })
        )
      );
    } catch (error) {
      console.error("Erro ao marcar todas como lidas:", error);
    }
  };

  if (!user) return null;

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon" className="relative">
          <Bell className="h-5 w-5" />
          {unreadCount > 0 && (
            <Badge
              variant="destructive"
              className="absolute -top-1 -right-1 h-5 w-5 flex items-center justify-center p-0 text-xs"
            >
              {unreadCount}
            </Badge>
          )}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-80">
        <DropdownMenuLabel className="flex items-center justify-between">
          <span>Notificações</span>
          {unreadCount > 0 && (
            <Button
              variant="ghost"
              size="sm"
              onClick={markAllAsRead}
              className="h-auto p-1 text-xs"
            >
              Marcar todas como lidas
            </Button>
          )}
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        <ScrollArea className="h-96">
          {notifications.length === 0 ? (
            <div className="p-4 text-center text-muted-foreground text-sm">
              Nenhuma notificação
            </div>
          ) : (
            notifications.map((notif) => (
              <DropdownMenuItem
                key={notif.id}
                className={`flex flex-col items-start p-3 cursor-pointer ${
                  !notif.read ? "bg-muted/50" : ""
                }`}
                onClick={() => !notif.read && markAsRead(notif.id)}
              >
                <div className="flex items-start justify-between w-full mb-1">
                  <span className="font-semibold text-sm">{notif.title}</span>
                  {!notif.read && (
                    <Badge variant="secondary" className="ml-2 h-2 w-2 p-0" />
                  )}
                </div>
                <p className="text-xs text-muted-foreground whitespace-normal">
                  {notif.message}
                </p>
                <span className="text-xs text-muted-foreground mt-1">
                  {notif.createdAt?.toDate?.().toLocaleDateString("pt-PT")}
                </span>
              </DropdownMenuItem>
            ))
          )}
        </ScrollArea>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};
