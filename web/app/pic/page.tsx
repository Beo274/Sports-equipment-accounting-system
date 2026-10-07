import Image from "next/image";

export default function PicPage() {
  return (
    <div className="w-full flex justify-center items-center">
      <Image
        src="/main.jpeg"
        alt="Motivational picture"
        width={300}
        height={300}
      />
    </div>
  );
}
