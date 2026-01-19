"use client"
import Multiplayer from "@/components/pages/multiplayer";
import Singleplayer from "@/components/pages/singleplayer";
import MPRenderer from "@/components/renderers/mprenderer";
import SPRenderer from "@/components/renderers/sprenderer";
import { BottomTabNavigator } from "@/components/ui/bottom-tab-navigator";
import { useConfig } from "@/lib/config";
import { UserRound, UsersRound } from "lucide-react";
import { useState } from "react";
import { create } from "zustand";

export default function Home() {
  const config = useConfig();
  const [activeScreen, setActiveScreen] = useState<number>(0)
  const tabs = [
    {
      value: "singleplayer",
      label: "Singleplayer",
      icon: UserRound,
      content: <Singleplayer />,
    },
    {
      value: "multiplayer",
      label: "Multiplayer",
      icon: UsersRound,
      content: <Multiplayer/>
    },
  ];

  return (
    <div>
      {activeScreen == 0? <BottomTabNavigator tabs={tabs}/>: config.config.kind == "sp"? <SPRenderer config={config.config.sp} />: <MPRenderer config={config.config.mp} />}
    </div>
  );
}
