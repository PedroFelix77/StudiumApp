import type { LucideIcon } from "lucide-react";
import { cn } from "@/lib/utils";

interface StatCardProps {
    title: string;
    value: number | string;
    icon: LucideIcon;
    description?: string;
    className?: string;
}

export function StatCard({
    title,
    value,
    icon: Icon,
    description,
    className,
}: StatCardProps) {
    return (
        <div
            className={cn(
                "bg-card text-card-foreground rounded-xl border p-6 shadow-sm",
                className
            )}
        >
            <div className="flex items-center justify-between">
                <div className="flex-1">
                    <p className="text-sm font-medium text-muted-foreground">{title}</p>
                    <p className="text-3xl font-bold mt-2">{value}</p>
                    {description && (
                        <p className="text-xs text-muted-foreground mt-1">{description}</p>
                    )}
                </div>
                <div className="ml-4">
                    <Icon className="h-8 w-8 text-muted-foreground" />
                </div>
            </div>
        </div>
    );
}


