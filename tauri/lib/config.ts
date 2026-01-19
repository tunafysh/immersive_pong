import { create } from "zustand";
import { Config, SPConfig, MPConfig } from "./types";

type ConfigState = {
  // Current config
  config: Config;

  // Singleplayer fields
  spType: "local" | "ai";
  spHard?: boolean;

  // Multiplayer fields
  mpRole?: "server" | "client";
  mpUrl?: string;

  // Setters
  setSP: (sp: SPConfig) => void;
  setMP: (mp: MPConfig) => void;
  reset: () => void;
};

export const useConfig = create<ConfigState>((set) => ({
  // Default: SP local
  config: { kind: "sp", sp: { type: "local" } },
  spType: "local",
  spHard: undefined,
  mpRole: undefined,
  mpUrl: undefined,

  // Update singleplayer config
  setSP: (sp: SPConfig) =>
    set(() => ({
      config: { kind: "sp", sp },
      spType: sp.type,
      spHard: sp.type === "ai" ? sp.hard : undefined,
      mpRole: undefined,
      mpUrl: undefined,
    })),

  // Update multiplayer config
  setMP: (mp: MPConfig) =>
    set(() => ({
      config: { kind: "mp", mp },
      mpRole: mp.role,
      mpUrl: mp.url,
      spType: undefined,
      spHard: undefined,
    })),

  // Reset to default SP local
  reset: () =>
    set(() => ({
      config: { kind: "sp", sp: { type: "local" } },
      spType: "local",
      spHard: undefined,
      mpRole: undefined,
      mpUrl: undefined,
    })),
}));
