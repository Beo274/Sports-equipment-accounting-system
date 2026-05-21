import { Dialog, DialogContent, DialogHeader } from "../ui/dialog";
import {
  Item,
  ItemActions,
  ItemDescription,
  ItemGroup,
  ItemTitle,
} from "../ui/item";
import { DialogEntity } from "./AddParameterDialog";
import { ClassParameter, ProductParameter } from "@/types/entityParameter";
import { useStore } from "@/lib/store/store";
import { useEffect } from "react";
import Loader from "../Loader/Loader";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import { EnumerationValue } from "@/types/enumeration";
import { Button } from "../ui/button";
import { Trash } from "lucide-react";

interface ListParametersDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  dialogEntity: DialogEntity;
}

export default function ListParametersDialog({
  open,
  onOpenChange,
  dialogEntity,
}: ListParametersDialogProps) {
  const {
    parameters: {
      isLoading,
      error,
      clearError,
      classParameters,
      productParameters,
      clearItems,
      fetchClassParameters,
      fetchProductParameters,
      deleteClassParameter,
      deleteProductParameter,
    },
  } = useStore();

  const getParams = () => {
    if (open && dialogEntity.entity) {
      switch (dialogEntity.paramFor) {
        case "class":
          fetchClassParameters(dialogEntity.entity.id);
          break;
        case "product":
          fetchProductParameters(dialogEntity.entity.id);
      }
    }
  };

  useEffect(() => {
    getParams();
    return () => {
      clearItems(dialogEntity.paramFor);
    };
  }, [open]);

  function renderItems() {
    let params;
    if (dialogEntity.paramFor === "class") {
      params = classParameters;
    } else {
      params = productParameters;
    }

    return isLoading ? (
      <Loader />
    ) : (
      <ItemGroup>
        {params.map((p) => (
          <ParameterItem
            key={p.id}
            entity={p}
            handleDelete={async () => {
              if (dialogEntity.paramFor === "class") {
                await deleteClassParameter(p.id);
              } else {
                await deleteProductParameter(p.id);
              }
              getParams();
            }}
          />
        ))}
      </ItemGroup>
    );
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>Параметры</DialogHeader>
        {dialogEntity.entity && renderItems()}
        {error && (
          <ErrorLabel
            message={error.message}
            onClearError={() => {
              clearError();
              getParams();
            }}
          />
        )}
      </DialogContent>
    </Dialog>
  );
}

interface ParameterItemProps {
  entity: ClassParameter | ProductParameter;

  handleDelete: () => Promise<void>;
}

function ParameterItem({ entity, handleDelete }: ParameterItemProps) {
  return (
    <Item className="grid grid-cols-3">
      <ItemTitle>{`${entity.param.name} (${entity.param.shortName})`}</ItemTitle>
      <ItemDescription>
        {entity.enumValue
          ? extractEnumValue(entity.enumValue)
          : `от ${entity.minVal} до ${entity.maxVal}`}
      </ItemDescription>
      <ItemActions className="justify-self-end">
        <Button
          className="hover:bg-accent"
          type="button"
          variant="secondary"
          onClick={handleDelete}
        >
          <Trash />
        </Button>
      </ItemActions>
    </Item>
  );
}

function extractEnumValue(ev: EnumerationValue) {
  if (ev.intValue) {
    return (
      <span>{`${ev.intValue} ${ev.measure ? ev.measure.shortName : ""}`}</span>
    );
  } else if (ev.stringValue) {
    return <span>{ev.stringValue}</span>;
  } else if (ev.imageValue) {
    return (
      <a href={ev.imageValue} target="_blank" className="hover:text-accent">
        Картинка
      </a>
    );
  } else {
    return <span>Значение не определено</span>;
  }
}
