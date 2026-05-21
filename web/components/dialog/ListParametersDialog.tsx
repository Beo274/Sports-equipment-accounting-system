import { Dialog, DialogContent, DialogHeader } from "../ui/dialog";
import { Item, ItemDescription, ItemGroup, ItemTitle } from "../ui/item";
import { DialogEntity } from "./AddParameterDialog";
import { ClassParameter, ProductParameter } from "@/types/entityParameter";
import { useStore } from "@/lib/store/store";
import { useEffect } from "react";
import Loader from "../Loader/Loader";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import { EnumerationValue } from "@/types/enumeration";

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
          <ParameterItem key={p.id} entity={p} />
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
}

function ParameterItem({ entity }: ParameterItemProps) {
  return (
    <Item>
      <ItemTitle>{`${entity.param.name} (${entity.param.shortName})`}</ItemTitle>
      <ItemDescription>
        {entity.enumValue
          ? extractEnumValue(entity.enumValue)
          : `Диапазон: ${entity.minVal} <-> ${entity.maxVal}`}
      </ItemDescription>
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
      <a href={ev.imageValue} className="hover:text-accent">
        Картинка
      </a>
    );
  } else {
    return <span>Значение не определено</span>;
  }
}
