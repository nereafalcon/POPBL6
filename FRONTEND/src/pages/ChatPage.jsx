import { useState } from "react";

const mockConversations = {
    "sandra": {
        name: "Sandra Olaiz",
        title: "Estudio Apartamentua UPV/EHU/tik gertu",
        messages: [
            {
                id: 1,
                sender: "Zu",
                role: "me",
                time: "10:30",
                text: "Kaixo, Sandra! UPV/EHUtik gertu dagoen estudioko apartamentua interesatzen zait. Gehiago esango zenidake?",
            },
            {
                id: 2,
                sender: "Sandra Olaiz",
                role: "other",
                time: "10:32",
                text: "Kaixo! Noski. Campusetik 5 minutura dago eta zerbitzu guztiak hartzen ditu. Ohea, idazmahaia, armairua eta sukaldea.",
            },
            {
                id: 3,
                sender: "Zu",
                role: "me",
                time: "10:34",
                text: "Bikain! Eta WiFi-a barne dago?",
            },
            {
                id: 4,
                sender: "Sandra Olaiz",
                role: "other",
                time: "10:35",
                text: "Bai noski! WiFi-a barne dago eta oso azkarra da.",
            },
        ],
    },
    "angel": {
        name: "Angel Izquierdo",
        title: "Etxe Partekatua Bilbon",
        messages: [
            {
                sender: "Zu",
                role: "me",
                time: "9:45",
                text: "Kaixo Angel! Etxea oraindik eskuragarri dago?",
            },
            {
                sender: "Angel Izquierdo",
                role: "other",
                time: "9:50",
                text: "Bai, interesatuta bazaude bihar bidaliko dizut kontratuaren xehetasunak.",
            },
        ],
    },
};

const ChatPage = () => {
    const [conversations, setConversations] = useState(mockConversations);
    const [selectedChatId, setSelectedChatId] = useState("sandra");
    const [input, setInput] = useState("");

    const handleSend = () => {
        if (!input.trim()) return;

        const newMsg = {
            sender: "You",
            role: "me",
            time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
            text: input.trim()
        };

        setConversations(prev => ({
            ...prev,
            [selectedChatId]: {
                ...prev[selectedChatId],
                messages: [...prev[selectedChatId].messages, newMsg]
            }
        }));

        setInput("");
    };


    return (
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex border h-[calc(100vh-10.25rem)]">
                {/* Sidebar */}
                <div className="w-full max-w-sm border-r flex flex-col">
                    <div className="p-4 border-b font-semibold text-lg">Mezuak</div>
                    <div className="flex flex-col">
                        {Object.entries(conversations).map(([id, convo]) => (
                            <div
                                key={id}
                                onClick={() => setSelectedChatId(id)}
                                className={`px-4 py-3 cursor-pointer hover:bg-gray-100 ${
                                    selectedChatId === id ? "bg-gray-100 font-medium" : ""
                                }`}
                            >
                                <div>{convo.name}</div>
                                <div className="text-sm text-gray-500 truncate">
                                    {convo.messages[convo.messages.length - 1].text}
                                </div>
                            </div>
                        ))}
                    </div>

                </div>

                {/* Chat Area */}
                <div className="flex-1 flex flex-col">
                    {/* Header */}
                    <div className="px-6 py-4 border-b">
                        <div className="font-semibold">{conversations[selectedChatId].name}</div>
                        <div className="text-sm text-gray-500">{conversations[selectedChatId].title}</div>
                    </div>

                    {/* Messages */}
                    <div className="flex-1 overflow-y-auto p-6 space-y-4">
                        {conversations[selectedChatId].messages.map(msg => (
                            <div
                                key={msg.id}
                                className={`max-w-xl text-sm px-4 py-3 rounded-lg ${
                                    msg.role === "me"
                                        ? "bg-blue-600 text-white ml-auto"
                                        : "bg-gray-100 text-gray-800"
                                }`}
                            >
                                {msg.text}
                                <div className="text-xs mt-1 text-right opacity-60">{msg.time}</div>
                            </div>
                        ))}
                    </div>

                    {/* Input */}
                    <div className="border-t p-4 flex gap-2">
                        <input
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={(e) => {
                                if (e.key === 'Enter' && !e.shiftKey) {
                                    e.preventDefault(); // prevent newline if using textarea
                                    handleSend();
                                }
                            }}
                            type="text"
                            placeholder="Idatzi mezu bat..."
                            className="flex-1 px-4 py-2 border rounded-lg"
                        />
                        <button
                            onClick={handleSend}
                            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
                        >
                            Bidali
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ChatPage;