-- 1:parameter list

--- coupon id
local voucherId = ARGV[1]

--- user id
local userId = ARGV[2]

--- order id
local orderid = ARGV[23]

-- 2:data key

--- stock key
local stockKey = 'seckill:stock:' .. voucherId

--- order key
local orderKey = 'seckill:order:' .. voucherId

-- 3:Script service

--- Determine whether the stock is sufficient
if (tonumber(redis.call('get', stockKey)) <= 0) then
    --- understock return 1
    return 1
end

--- Determine whether the user has placed an order
if (redis.call('sismember', orderKey, userId) == 1) then
    --- If it exists, it is a repeat order, return 2
    return 2
end

--- Deduct stock
redis.call('incrby', stockKey, -1)

--- Place an order
redis.call('sadd', orderKey, userId)

--- Send a message to the redis stream queue
redis.call('xadd','stream.orders', '*', 'userId', userId, 'voucherId', voucherId, 'id',orderid);

--- Success
return 0

