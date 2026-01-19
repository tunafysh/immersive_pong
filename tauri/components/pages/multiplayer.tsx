import { useState } from "react";
import { Button } from "../ui/button";
import { Config } from "@/lib/types";
import { useConfig } from "@/lib/config";

export default function Multiplayer(){
    const config = useConfig();

    const createRoom = () => {
        config.setMP({role: "server", url: ""})
    }

    const joinRoom = () => {
        config.setMP({role: "client", url: ""})
    }

    return (
        <div className="w-full h-full flex flex-col justify-center items-center gap-4">   
            <h1 className="font-bold text-xl">Welcome to Immersive Pong</h1>
            <div className="flex flex-col gap-2">
                <Button onClick={() => createRoom} className="font-bold">Create a room</Button>
                <Button onClick={() => joinRoom()}className="font-bold">Join Room</Button>
            </div>
        </div>
    )
}