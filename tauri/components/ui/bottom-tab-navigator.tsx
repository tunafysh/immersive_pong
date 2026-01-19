"use client";

import * as React from "react";
import {
  Tabs,
  TabsList,
  TabsTrigger,
  TabsContent,
} from "@/components/ui/tabs";
import { cn } from "@/lib/utils";
import { BottomTab } from "@/lib/types";

export function BottomTabNavigator({ tabs }: { tabs: BottomTab[] }) {
  return (
    <Tabs
      defaultValue={tabs[0].value}
      className="flex h-screen flex-col bg-background"
    >
      {/* Tab panels */}
      <div className="flex-1 overflow-auto">
        {tabs.map((tab) => (
          <TabsContent
            key={tab.value}
            value={tab.value}
            className="h-full p-4"
          >
            {tab.content}
          </TabsContent>
        ))}
      </div>

      {/* Bottom tab bar */}
      <div className="sticky bottom-0 border-t bg-background w-full">
        <TabsList className="flex h-16 justify-around w-full">
          {tabs.map((tab) => (
            <TabsTrigger
              key={tab.value}
              value={tab.value}
              className={cn(
                "group flex flex-col items-center justify-center gap-1",
                "rounded-md px-3 py-2 transition",
                "data-[state=active]:bg-accent",
                "data-[state=active]:text-primary"
              )}
            >
              {/* Icon */}
              <tab.icon
                className={cn(
                  "h-6 w-6 transition-transform",
                  "group-data-[state=active]:scale-110"
                )}
              />

              {/* Label */}
              <span className="text-xs font-medium">
                {tab.label}
              </span>

              {/* Active indicator */}
              <span
                className={cn(
                  "h-0.5 w-6 rounded-full bg-transparent transition",
                  "group-data-[state=active]:bg-primary"
                )}
              />
            </TabsTrigger>
          ))}
        </TabsList>
      </div>
    </Tabs>
  );
}
