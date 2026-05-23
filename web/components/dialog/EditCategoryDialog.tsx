import MeasureUnit, { NullMeasureUnit } from "@/types/measureUnit";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../ui/dialog";
import { Field, FieldLabel } from "../ui/field";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { Category } from "@/types/category";
import { useEffect, useRef } from "react";
import { useStore } from "@/lib/store/store";
import { Item } from "../ui/item";
import Loader from "../Loader/Loader";

interface EditCategoryDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;

  category: Category | null;
}

export default function EditCategoryDialog({
  open,
  onOpenChange,
  category,
}: EditCategoryDialogProps) {
  const {
    categories: {
      refreshList,
      changeMeasure,
      changeBaseClass,
      deleteBaseClass,
      deleteMeasure,
      isLoading,
      editing: { baseClassId, measureUnit, setBaseClassId, setMeasureUnit },
    },
    measures: { items: measures, isLoading: isLoadingMeasures, fetchMeasures },
  } = useStore();
  const buttonRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    if (category) {
      setBaseClassId(
        category.baseClassId !== null ? String(category.baseClassId) : "",
      );
      setMeasureUnit(
        category.mUnitId ? String(category.mUnitId) : NullMeasureUnit,
      );
    }
  }, [category]);

  useEffect(() => {
    if (open && !isLoadingMeasures && !measures.length) {
      fetchMeasures();
    }
  }, [open]);

  const handleMeasureUnitChange = (value: string | null) => {
    setMeasureUnit(value ?? NullMeasureUnit);
  };

  const handleEdit = async () => {
    if (!category) return;

    const newMeasureUnit =
      measureUnit === NullMeasureUnit ? null : Number(measureUnit);
    const newBaseClassId = baseClassId === "" ? null : Number(baseClassId);
    let changed = false;

    if (newMeasureUnit !== category.mUnitId) {
      if (newMeasureUnit !== null && !isNaN(newMeasureUnit)) {
        await changeMeasure(category.id, newMeasureUnit);
      } else {
        await deleteMeasure(category.id);
      }
      changed = true;
    }

    if (newBaseClassId !== category.baseClassId) {
      if (newBaseClassId !== null && !isNaN(newBaseClassId)) {
        await changeBaseClass(category.id, newBaseClassId);
      } else {
        await deleteBaseClass(category.id);
      }
      changed = true;
    }

    if (changed) {
      await refreshList();
      onOpenChange(false);
    } else {
      if (buttonRef.current) {
        buttonRef.current.textContent = "Изменять нечего";
        setTimeout(() => {
          if (buttonRef.current) {
            buttonRef.current.textContent = "Сохранить";
          }
        }, 3000);
      }
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Редактирование класса</DialogTitle>
        </DialogHeader>
        <Field>
          <FieldLabel>Выбор единицы измерения</FieldLabel>
          {isLoadingMeasures ? (
            <Item>
              <Loader />
            </Item>
          ) : (
            <Select onValueChange={handleMeasureUnitChange} value={measureUnit}>
              <SelectTrigger>
                <SelectValue
                  placeholder="Единица измерения"
                  className="text-gray-400"
                >
                  {measureUnit === NullMeasureUnit ? (
                    <span>{NullMeasureUnit}</span>
                  ) : (
                    (() => {
                      const selected = measures.find(
                        (m) => String(m.id) === measureUnit,
                      );
                      return selected ? (
                        <span>{`${selected.name} (${selected.shortName})`}</span>
                      ) : null;
                    })()
                  )}
                </SelectValue>
              </SelectTrigger>
              <SelectContent>
                <SelectGroup>
                  <SelectLabel>Единицы измерения категории</SelectLabel>
                  <SelectItem value={NullMeasureUnit}>Без е. и.</SelectItem>
                  {measures.map((m) => (
                    <SelectItem key={m.id} value={String(m.id)}>
                      {`${m.name} (${m.shortName})`}
                    </SelectItem>
                  ))}
                </SelectGroup>
              </SelectContent>
            </Select>
          )}
        </Field>
        <Field>
          <Input
            id="category-edit-baseClass-id"
            type="number"
            min={1}
            value={baseClassId ?? ""}
            onChange={(e) => setBaseClassId(e.target.value)}
            placeholder="Нет базовой категории"
            className="placeholder:text-gray-400"
          />
        </Field>
        <Button
          variant="secondary"
          className="hover:bg-accent"
          type="submit"
          disabled={isLoading}
          ref={buttonRef}
          onClick={handleEdit}
        >
          Сохранить
        </Button>
      </DialogContent>
    </Dialog>
  );
}
