import { useState, useEffect } from "react";

interface SearchBoxProps {
  value: string;
  onChange: (keyword: string) => void;
  placeholder?: string;
}

const SearchBox = ({ value, onChange, placeholder = "Tìm theo tên hoặc mã môn học..." }: SearchBoxProps) => {
  const [localValue, setLocalValue] = useState(value);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setLocalValue(value);
  }, [value]);

  useEffect(() => {
    const handler = setTimeout(() => {
      if (localValue !== value) {
        onChange(localValue);
      }
    }, 400);

    return () => {
      clearTimeout(handler);
    };
  }, [localValue, onChange, value]);

  return (
    <div className="search-box">
      <span className="search-icon">🔍</span>
      <input
        type="text"
        value={localValue}
        onChange={(e) => setLocalValue(e.target.value)}
        placeholder={placeholder}
        className="search-input"
      />
    </div>
  );
};

export default SearchBox;
