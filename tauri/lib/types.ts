export type BottomTab = {
  value: string;
  label: string;
  icon: React.ComponentType<React.SVGProps<SVGSVGElement>>;
  content: React.ReactNode;
};

export type MPConfig = {
  role: "server" | "client";
  url: string
}

export type SPConfig =
  | { type: "local" }
  | { type: "ai"; hard: boolean };

export type Config  = 
  | { kind: "sp"; sp: SPConfig }
  | { kind: "mp"; mp: MPConfig }

export type Vector2 = { x: number; y: number };

export type Paddle = {
  posX: number;    // horizontal position
  width: number;
  height: number;
  speed: number;   // pixels per frame for AI/user movement
};

export type Ball = {
  posX: number;
  posY: number;
  velX: number;
  velY: number;
  size: number;
};

export type GameState = {
  ball: Ball;
  topPaddle: Paddle;     // AI / opponent
  bottomPaddle: Paddle;  // user
  width: number;         // canvas width
  height: number;        // canvas height
};
