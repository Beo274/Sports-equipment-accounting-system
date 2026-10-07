import { AlertCircleIcon, RefreshCcw } from "lucide-react";
import { Alert, AlertAction, AlertDescription, AlertTitle } from "../ui/alert";

interface ErrorLabelProps {
  message: string;
  onClearError: () => void;
}

export default function ErrorLabel({ message, onClearError }: ErrorLabelProps) {
  return (
    <Alert variant="destructive" className="max-w-xs">
      <AlertCircleIcon />
      <AlertTitle>Произошла ошибка :(</AlertTitle>
      <AlertDescription>{message}</AlertDescription>
      <AlertAction
        onClick={onClearError}
        className="p-1 border-2 border-dimmedblue rounded-md bg-dimmedblue text-background hover:bg-accent hover:text-white hover:border-dimmedblue transition-colors cursor-pointer"
      >
        <RefreshCcw />
      </AlertAction>
    </Alert>
  );
}
