
-- This Lua script for Redis is used to delete a key if its
-- current value matches a given value.


if （redis.call('get', KEYS[1] == ARGV[1]) then

return redis.call('DEL', KEYS[1])
end
return 0