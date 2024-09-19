#!/bin/bash

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

environment_script_file="$script_dir/nami_log_environment.sh"
# shellcheck disable=SC1090
source "$environment_script_file"


convert_key_to_32_bytes() {
  local key="$1"

  # Convert the key to its UTF-8 hex representation
  local key_hex=$(echo -n "$key" | xxd -p | tr -d '\n')

  # Ensure the hex representation is exactly 64 characters (32 bytes)
  if [ ${#key_hex} -lt 64 ]; then
    # Pad with zeros if less than 64 characters
    while [ ${#key_hex} -lt 64 ]; do
      key_hex="${key_hex}00"
    done
  elif [ ${#key_hex} -gt 64 ]; then
    # Truncate to 64 characters if longer
    key_hex=$(echo "$key_hex" | cut -c 1-64)
  fi

  echo "$key_hex"
}

convert_iv_to_16_bytes() {
  local iv="$1"

  # Convert the IV to UTF-8 and then truncate or pad to exactly 16 bytes (32 hex characters)
  # Convert the IV to hex
  local iv_hex=$(echo -n "$iv" | xxd -p | tr -d '\n')

  # Ensure the hex representation is exactly 32 characters (16 bytes)
  if [ ${#iv_hex} -lt 32 ]; then
    # Pad with zeros if less than 32 characters
    while [ ${#iv_hex} -lt 32 ]; do
      iv_hex="${iv_hex}00"
    done
  elif [ ${#iv_hex} -gt 32 ]; then
    # Truncate to 32 characters if longer
    iv_hex=$(echo "$iv_hex" | cut -c 1-32)
  fi

  echo "$iv_hex"
}

converted_key=$(convert_key_to_32_bytes "$ENCRYPTION_KEY")
converted_iv=$(convert_iv_to_16_bytes "$IV")

# Function to decrypt the log message
decrypt_message() {
    local encrypted_message="$1"
    # Replace this with actual decryption logic
    echo "$encrypted_message" | openssl enc -aes-256-cbc -d -a -K "$converted_key" -iv "$converted_iv"
}

# Capture logs and process each line
/Users/nhatvu/Library/Android/sdk/platform-tools/adb logcat -s tag-namisdk:E | while read -r line; do
  echo "start logging"
    # Extract the encrypted message from the log
    encrypted_message=$(echo "$line" | awk '{print $NF}')

    # Decrypt the message
    decrypted_message=$(decrypt_message "$encrypted_message")

    # Print the decrypted message along with the original log details
    echo "$decrypted_message"
done