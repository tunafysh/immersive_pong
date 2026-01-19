import { useConfig } from "@/lib/config";
import { Button } from "../ui/button";
import { DrawerTrigger, DrawerContent, DrawerHeader, DrawerTitle, DrawerDescription, Drawer } from "../ui/drawer";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../ui/select";
import { Label } from "@/components/ui/label";
import { SPConfig } from "@/lib/types";
import { Switch } from "../ui/switch";

export default function Singleplayer(){
    const config = useConfig();

    
    return (
        <div className="w-full h-full flex flex-col justify-center items-center gap-4">   
            <h1 className="font-bold text-xl">Welcome to Immersive Pong</h1>
            <div className="flex flex-col gap-2">
                <Drawer>
                    <DrawerTrigger asChild>
                        <Button variant={"outline"} className="font-bold">Options</Button>
                    </DrawerTrigger>
                    <DrawerContent>
                        <DrawerHeader>
                            <DrawerTitle>Game Options</DrawerTitle>
                            <DrawerDescription>
                                Configure your singleplayer game settings.
                            </DrawerDescription>
                        </DrawerHeader>
                        <div className="flex flex-col gap-6 py-8 px-6 max-w-sm mx-auto">
  {/* Mode */}
  <div className="flex items-center justify-between">
    <Label htmlFor="ai-mode">AI Opponent</Label>
    <Switch
      id="ai-mode"
      checked={config.spType === "ai"}
      onCheckedChange={(checked: boolean) =>{
        var conf = checked ? {type: "ai"} : {type: "local"}
        config.setSP(conf as SPConfig);
      }}
    />
  </div>

  {/* Difficulty */}
  <div className="flex flex-col space-y-1.5">
    {/* Difficulty */}
<div className="flex items-center justify-between">
  <Label
    htmlFor="difficulty"
    className={config.spType !== "ai" ? "opacity-50" : ""}
  >
    Difficulty: 
  </Label>

  <div className="flex items-center gap-3">
    <span
      className={`text-sm font-medium transition-colors ${
        config.spHard
          ? "text-red-500"
          : "text-green-500"
      } ${config.spType !== "ai" ? "opacity-50" : ""}`}
    >
      {config.spHard ? "Hard" : "Easy"}
    </span>

    <Switch
      id="difficulty"
      checked={config.spHard}
      disabled={config.spType !== "ai"}
      onCheckedChange={(checked: boolean) => {
        var conf = { type: "ai", hard: checked} as SPConfig;
        config.setSP(conf);
      }}
      className={
        config.spHard
          ? "data-[state=checked]:bg-red-500"
          : "data-[state=checked]:bg-green-500"
      }
    />
  </div>
</div>
      <p
  className={`text-xs leading-snug ${
    config.spHard ? "text-foreground" : "text-background"
  }`}
>
  Note: Hard mode ramps up over time as the AI learns your play patterns.
</p>


  </div>
</div>

                    </DrawerContent>
                </Drawer>
                <Button className="font-bold">Start Game</Button>
            </div>
        </div>
    )
}